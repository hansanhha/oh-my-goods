package co.ohmygoods.review.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.review.exception.ReviewException;
import co.ohmygoods.review.model.entity.Review;
import co.ohmygoods.review.model.entity.ReviewComment;
import co.ohmygoods.review.repository.ReviewCommentRepository;
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


@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewImageService reviewImageService;
    private final AccountRepository accountRepository;
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCommentRepository reviewCommentRepository;

    public Slice<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        Slice<Review> reviews = reviewRepository.fetchAllReviwerByProductId(productId, pageable);

        return reviews.map(ReviewResponse::from);
    }

    public Slice<ReviewCommentResponse> getReviewComments(Long reviewId, Pageable pageable) {
        Slice<ReviewComment> reviewComments = reviewCommentRepository.fetchAllWriterByReviewId(reviewId, pageable);

        return reviewComments.map(rc -> ReviewCommentResponse.from(reviewId, 0L, rc));
    }

    public Slice<ReviewCommentResponse> getReviewReplyComments(Long reviewId, Long reviewCommentId, Pageable pageable) {
        Slice<ReviewComment> reviewComments =
                reviewCommentRepository.fetchAllWriterByReviewIdAndReviewCommentId(reviewId, reviewCommentId, pageable);

        return reviewComments.map(reviewComment -> ReviewCommentResponse.from(reviewId, reviewCommentId, reviewComment));
    }

    public Long writeReview(WriteReviewRequest request) {
        Account account = accountRepository.findByMemberId(request.memberId()).orElseThrow(AuthException::notFoundAccount);
        OrderItem orderItem = orderItemRepository.fetchProductByOrderNumber(request.reviewOrderNumber()).orElseThrow(OrderException::notFoundOrderItem);

        /*
            하나의 주문 건엔 하나의 리뷰만 남길 수 있음
            해당 주문 건에 대해 이미 작성한 리뷰가 있는 경우 예외 발생
         */
        reviewRepository.findReviewByOrderItem(orderItem).ifPresent(review -> {
            throw ReviewException.ALREADY_WRITTEN_REVIEW;
        });

        if (!orderItem.getOrder().isOrderer(account)) {
            throw ReviewException.INVALID_AUTHORITY_WRITE_REVIEW;
        }

        Review savedReview = Review.create(orderItem, orderItem.getProduct(), account, request.reviewContent(), request.reviewStarRating());

        if (!request.images().isEmpty()) {
            reviewImageService.upload(savedReview, account.getEmail(), request.storageStrategy(), request.images());
        }

        Review review = reviewRepository.save(savedReview);
        return review.getId();
    }

    public void updateReview(UpdateReviewRequest request) {
        Review review = reviewRepository.findById(request.updateReviewId()).orElseThrow(ReviewException::notFoundReview);
        Account account = accountRepository.findByMemberId(request.memberId()).orElseThrow(AuthException::notFoundAccount);

        if (!review.isNotReviewer(account)) {
            throw ReviewException.INVALID_AUTHORITY_WRITE_REVIEW;
        }

        review.update(request.reviewStarRating(), request.updateReviewContent());

        if (request.isUpdatedReviewImages()) {

            // 기존 리뷰 이미지 삭제
            reviewImageService.delete(review);

            // 새 리뷰 이미지 업로드
            if (!request.updateReviewImages().isEmpty()) {
                reviewImageService.upload(review, account.getEmail(), StorageStrategy.CLOUD_STORAGE_API, request.updateReviewImages());
            }
        }
    }

    public void deleteReview(Long reviewId, String memberId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewException::notFoundReview);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        if (review.isNotReviewer(account)) {
            throw ReviewException.INVALID_AUTHORITY_WRITE_REVIEW;
        }

        reviewRepository.delete(review);
        reviewImageService.delete(review);
    }

    public void writeReviewComment(Long reviewId, String memberId, String reviewCommentContent) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewException::notFoundReview);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        ReviewComment reviewComment = ReviewComment.create(review, account, reviewCommentContent);

        reviewCommentRepository.save(reviewComment);
    }

    public void updateReviewComment(Long reviewCommentId, String memberId, String updateCommentContent) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId).orElseThrow(ReviewException::notFoundReview);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        if (!reviewComment.isNotReviewCommenter(account)) {
            throw ReviewException.INVALID_AUTHORITY_WRITE_REVIEW_COMMENT;
        }

        reviewComment.update(updateCommentContent);
    }

    public void deleteReviewComment(Long reviewCommentId, String memberId) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId).orElseThrow(ReviewException::notFoundReview);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        if (reviewComment.isNotReviewCommenter(account)) {
            throw ReviewException.INVALID_AUTHORITY_WRITE_REVIEW_COMMENT;
        }

        reviewCommentRepository.delete(reviewComment);
    }

    public void writeReviewReplyComment(Long reviewCommentId, String memberId, String reviewReplyCommentContent) {
        ReviewComment reviewComment = reviewCommentRepository.findById(reviewCommentId).orElseThrow(ReviewException::notFoundReviewComment);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        ReviewComment replyComment = ReviewComment.create(reviewComment, account, reviewReplyCommentContent);

        reviewCommentRepository.save(replyComment);
    }

}
