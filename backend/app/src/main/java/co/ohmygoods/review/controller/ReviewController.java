package co.ohmygoods.review.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.file.model.vo.StorageStrategy;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.review.controller.dto.UpdateReviewCommentWebRequest;
import co.ohmygoods.review.controller.dto.UpdateReviewWebRequest;
import co.ohmygoods.review.controller.dto.WriteReviewCommentWebRequest;
import co.ohmygoods.review.controller.dto.WriteReviewWebRequest;
import co.ohmygoods.review.service.ReviewService;
import co.ohmygoods.review.service.dto.UpdateReviewRequest;
import co.ohmygoods.review.service.dto.WriteReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@RequestMapping("/api/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}")
    public void getReviews(@PathVariable("productId") Long productId,
                           @RequestParam(required = false, defaultValue = "0") int page,
                           @RequestParam(required = false, defaultValue = "20") int size) {

        reviewService.getReviews(productId, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/{reviewId}/comments")
    public void getReviewComments(@PathVariable("reviewId") Long reviewId,
                                  @RequestParam(required = false, defaultValue = "0") int page,
                                  @RequestParam(required = false, defaultValue = "20") int size) {

        reviewService.getReviewComments(reviewId, Pageable.ofSize(size).withPage(page));
    }

    @GetMapping("/{reviewId}/comments/{commentId}/replies")
    public void getReviewReplyComments(@PathVariable("reviewId") Long reviewId,
                                       @PathVariable("commentId") Long commentId,
                                       @RequestParam(required = false, defaultValue = "0") int page,
                                       @RequestParam(required = false, defaultValue = "20") int size) {

        reviewService.getReviewReplyComments(reviewId, commentId, Pageable.ofSize(size).withPage(page));
    }

    @PostMapping
    @Idempotent
    public void writeReview(@AuthenticationPrincipal AuthenticatedAccount account,
                            @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                            @RequestBody @Validated WriteReviewWebRequest request) {

        WriteReviewRequest writeReviewRequest = new WriteReviewRequest(request.reviewOrderNumber(),
                account.memberId(), request.reviewContent(), request.reviewStarRating(), request.reviewImages(),
                request.storageStrategy() != null ? request.storageStrategy() : StorageStrategy.CLOUD_STORAGE_API);

        reviewService.writeReview(writeReviewRequest);
    }

    @PostMapping("/{reviewId}/comment")
    @Idempotent
    public void writeReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                   @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                   @PathVariable("reviewId") Long reviewId,
                                   @RequestBody @Validated WriteReviewCommentWebRequest request) {

        reviewService.writeReviewComment(reviewId, account.memberId(), request.reviewCommentContent());
    }

    @PostMapping("/{reviewId}/comments/{commentId}/reply")
    @Idempotent
    public void writeReviewCommentReply(@AuthenticationPrincipal AuthenticatedAccount account,
                                        @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                        @PathVariable("reviewId") Long reviewId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestBody @Validated WriteReviewCommentWebRequest request) {

        reviewService.writeReviewReplyComment(commentId, account.memberId(), request.reviewCommentContent());
    }

    @PatchMapping("/{reviewId}")
    @Idempotent
    public void updateReview(@AuthenticationPrincipal AuthenticatedAccount account,
                             @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                             @PathVariable("reviewId") Long reviewId,
                             @RequestBody @Validated UpdateReviewWebRequest request) {

        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest(reviewId, account.memberId(),
                request.updateReviewStarRating(), request.updateReviewContent(),
                (request.updateReviewImages() != null && !request.updateReviewImages().isEmpty()) || request.isUpdatedReviewImages(),
                request.updateReviewImages(), request.storageStrategy() != null ? request.storageStrategy() : StorageStrategy.CLOUD_STORAGE_API);

        reviewService.updateReview(updateReviewRequest);
    }

    @PatchMapping("/{reviewId}/comments/{commentId}")
    @Idempotent
    public void updateReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                    @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                    @PathVariable("reviewId") Long reviewId,
                                    @PathVariable("commentId") Long commentId,
                                    @RequestBody @Validated UpdateReviewCommentWebRequest request) {

        reviewService.updateReviewComment(commentId, account.memberId(), request.updateReviewCommentContent());
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@AuthenticationPrincipal AuthenticatedAccount account,
                             @PathVariable("reviewId") Long reviewId) {

        reviewService.deleteReview(reviewId, account.memberId());
    }

    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public void deleteReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                    @PathVariable("reviewId") Long reviewId,
                                    @PathVariable("commentId") Long commentId) {

        reviewService.deleteReviewComment(commentId, account.memberId());
    }
}
