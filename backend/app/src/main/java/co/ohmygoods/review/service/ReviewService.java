package co.ohmygoods.review.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.file.model.vo.StorageStrategy;
import co.ohmygoods.file.service.FileService;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.review.exception.ReviewException;
import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewComment;
import co.ohmygoods.review.repository.ReviewCommentRepository;
import co.ohmygoods.review.repository.ReviewImageInfoRepository;
import co.ohmygoods.review.repository.ReviewRepository;
import co.ohmygoods.review.service.dto.ReviewCommentResponse;
import co.ohmygoods.review.service.dto.ReviewResponse;
import co.ohmygoods.review.service.dto.UpdateReviewRequest;
import co.ohmygoods.review.service.dto.WriteReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final FileService fileService;
    private final ReviewImageService reviewImageService;
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageInfoRepository reviewImageInfoRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    public List<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.fetchReviewerAllByProductId(productId, pageable);

        return reviews.stream()
                .map(review -> ReviewResponse.from(productId, review))
                .toList();
    }

    public List<ReviewCommentResponse> getReviewComments(Long reviewId, Pageable pageable) {
        Slice<ReviewComment> reviewComments = reviewCommentRepository.fetchWriterAllByReviewId(reviewId, pageable);

        return reviewComments.stream()
                .map(reviewComment -> ReviewCommentResponse.from(reviewId, 0L, reviewComment))
                .toList();
    }

    public List<ReviewCommentResponse> getReviewReplyComments(Long reviewId, Long reviewCommentId, Pageable pageable) {
        Slice<ReviewComment> reviewComments =
                reviewCommentRepository.fetchWriterAllByReviewIdAndReviewCommentId(reviewId, reviewCommentId, pageable);

        return reviewComments.stream()
                .map(reviewComment -> ReviewCommentResponse.from(reviewId, reviewCommentId, reviewComment))
                .toList();
    }

    public void writeReview(WriteReviewRequest request) {
        OAuth2Account account = accountRepository.findByEmail(request.accountEmail())
                .orElseThrow(ReviewException::notFoundAccount);

        Order order = orderRepository.fetchProductByOrderNumber(request.reviewOrderNumber())
                .orElseThrow(ReviewException::notFoundOrder);

        /*
            하나의 주문 건엔 하나의 리뷰만 남길 수 있음
            해당 주문 건에 대해 이미 작성한 리뷰가 있는 경우 예외 발생
         */
        reviewRepository.findReviewByOrder(order).ifPresent(review -> {
            throw ReviewException.alreadyWriteReview();
        });

        if (!order.isOrderer(account)) {
            throw ReviewException.invalidReviewAuthority();
        }

        Review savedReview = Review.write(order, order.getProduct(), account, request.reviewContent(), request.reviewStarRating());

        // 리뷰 이미지 업로드
        if (!request.reviewImages().isEmpty()) {
            reviewImageService.upload(savedReview.getId(), account.getEmail(), request.storageStrategy(), request.reviewImages());
        }

        reviewRepository.save(savedReview);
    }

    public void updateReview(UpdateReviewRequest request) {
        Review review = reviewRepository.findById(request.updateReviewId())
                .orElseThrow(ReviewException::notFoundReview);

        OAuth2Account account = accountRepository.findByEmail(request.reviewerEmail())
                .orElseThrow(ReviewException::notFoundAccount);

        if (!review.isNotReviewer(account)) {
            throw ReviewException.invalidReviewAuthority();
        }

        review.update(request.updateReviewContent());

        if (request.isUpdatedReviewImages()) {

            // 기존 리뷰 이미지 삭제
            reviewImageService.delete(review.getId());

            // 새 리뷰 이미지 업로드
            if (!request.updateReviewImages().isEmpty()) {
                reviewImageService.upload(review.getId(), account.getEmail(), StorageStrategy.CLOUD_STORAGE_API, request.updateReviewImages());
            }
        }
    }

    public void deleteReview(Long reviewId, String reviewerEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewException::notFoundReview);

        OAuth2Account account = accountRepository.findByEmail(reviewerEmail)
                .orElseThrow(ReviewException::notFoundAccount);

        if (review.isNotReviewer(account)) {
            throw ReviewException.invalidReviewAuthority();
        }

        reviewRepository.delete(review);
        reviewImageService.delete(reviewId);
    }

    public void writeReviewComment(Long reviewId, String accountEmail, String reviewCommentContent) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewException::notFoundReview);

        OAuth2Account account = accountRepository.findByEmail(accountEmail)
                .orElseThrow(ReviewException::notFoundAccount);

        ReviewComment reviewComment = ReviewComment.write(review, account, reviewCommentContent);

        reviewCommentRepository.save(reviewComment);
    }

    public void modifyReviewComment(Long reviewCommentId, String reviewCommenterEmail, String modifyReviewCommentContent) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId)
                .orElseThrow(ReviewException::notFoundReview);

        OAuth2Account account = accountRepository.findByEmail(reviewCommenterEmail)
                .orElseThrow(ReviewException::notFoundAccount);

        if (!reviewComment.isNotReviewCommenter(account)) {
            throw ReviewException.invalidReviewCommentAuthority();
        }

        reviewComment.update(modifyReviewCommentContent);
    }

    public void deleteReviewComment(Long reviewCommentId, String reviewCommenterEmail) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId)
                .orElseThrow(ReviewException::notFoundReview);

        OAuth2Account account = accountRepository.findByEmail(reviewCommenterEmail)
                .orElseThrow(ReviewException::notFoundAccount);

        if (reviewComment.isNotReviewCommenter(account)) {
            throw ReviewException.invalidReviewAuthority();
        }

        reviewCommentRepository.delete(reviewComment);
    }

    public void writeReviewReplyComment(Long reviewCommentId, String accountEmail, String reviewReplyCommentContent) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId)
                .orElseThrow(ReviewException::notFoundReviewComment);

        OAuth2Account account = accountRepository.findByEmail(accountEmail)
                .orElseThrow(ReviewException::notFoundAccount);

        ReviewComment reply = ReviewComment.reply(reviewComment, account, reviewReplyCommentContent);

        reviewCommentRepository.save(reply);
    }

}
