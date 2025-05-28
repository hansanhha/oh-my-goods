package co.ohmygoods.review.controller;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.file.model.vo.StorageStrategy;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.review.controller.dto.UpdateReviewCommentWebRequest;
import co.ohmygoods.review.controller.dto.UpdateReviewWebRequest;
import co.ohmygoods.review.controller.dto.WriteReviewCommentWebRequest;
import co.ohmygoods.review.controller.dto.WriteReviewWebRequest;
import co.ohmygoods.review.service.ReviewService;
import co.ohmygoods.review.service.dto.ReviewCommentResponse;
import co.ohmygoods.review.service.dto.ReviewResponse;
import co.ohmygoods.review.service.dto.UpdateReviewRequest;
import co.ohmygoods.review.service.dto.WriteReviewRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;


@Tag(name = "리뷰", description = "리뷰 관련 api")
@RequestMapping("/api/reviews")
@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "상품의 리뷰 내역 조회", description = "특정 상품에 남겨진 리뷰 목록을 조회합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 내역 반환")
    )
    @GetMapping("/products/{productId}")
    public ResponseEntity<Slice<ReviewResponse>> getReviews(@Parameter(name = "상품 아이디", in = ParameterIn.PATH) @PathVariable("productId") Long productId,
                                                            @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                                            @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        return ResponseEntity.ok(reviewService.getReviews(productId, Pageable.ofSize(size).withPage(page)));
    }

    @Operation(summary = "리뷰 댓글 조회", description = "특정 리뷰에 남겨진 댓글 목록을 조회합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "댓글 목록 반환")
    )
    @GetMapping("/{reviewId}/comments")
    public ResponseEntity<Slice<ReviewCommentResponse>> getReviewComments(@Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                                                          @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                                                          @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        return ResponseEntity.ok(reviewService.getReviewComments(reviewId, Pageable.ofSize(size).withPage(page)));
    }

    @Operation(summary = "리뷰 대댓글 조회", description = "특정 리뷰 댓글의 대댓글 목록을 조회합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "대댓글 목록 반환")
    )
    @GetMapping("/{reviewId}/comments/{commentId}/replies")
    public ResponseEntity<Slice<ReviewCommentResponse>> getReviewReplyComments(@Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                       @Parameter(name = "리뷰 댓글 아이디", in = ParameterIn.PATH) @PathVariable("commentId") Long commentId,
                                       @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                       @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        return ResponseEntity.ok(reviewService.getReviewReplyComments(reviewId, commentId, Pageable.ofSize(size).withPage(page)));
    }

    @Operation(summary = "리뷰 작성", description = "상품의 구매자가 리뷰를 작성합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 작성 완료")
    )
    @PostMapping
    @Idempotent
    public ResponseEntity<?> writeReview(@AuthenticationPrincipal AuthenticatedAccount account,
                            @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                            @RequestBody @Validated WriteReviewWebRequest request) {

        WriteReviewRequest writeReviewRequest = new WriteReviewRequest(request.reviewOrderNumber(),
                account.memberId(), request.reviewContent(), request.reviewStarRating(), request.reviewImages(),
                request.storageStrategy() != null ? request.storageStrategy() : StorageStrategy.CLOUD_STORAGE_API);

        Long reviewId = reviewService.writeReview(writeReviewRequest);
        return ResponseEntity.created(URI.create("/api/reviews/" + reviewId)).build();
    }

    @Operation(summary = "리뷰 댓글 작성", description = "특정 리뷰에 댓글을 작성합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 댓글 작성 완료")
    )
    @PostMapping("/{reviewId}/comment")
    @Idempotent
    public void writeReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                   @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                   @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                   @RequestBody @Validated WriteReviewCommentWebRequest request) {

        reviewService.writeReviewComment(reviewId, account.memberId(), request.reviewCommentContent());
    }

    @Operation(summary = "리뷰 대댓글 작성", description = "특정 리뷰 댓글에 대댓글을 작성합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 대댓글 작성 완료")
    )
    @PostMapping("/{reviewId}/comments/{commentId}/reply")
    @Idempotent
    public void writeReviewCommentReply(@AuthenticationPrincipal AuthenticatedAccount account,
                                        @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                        @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                        @Parameter(name = "리뷰 댓글 아이디", in = ParameterIn.PATH) @PathVariable("commentId") Long commentId,
                                        @RequestBody @Validated WriteReviewCommentWebRequest request) {

        reviewService.writeReviewReplyComment(commentId, account.memberId(), request.reviewCommentContent());
    }

    @Operation(summary = "리뷰 수정", description = "리뷰 작성자가 리뷰를 수정합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 수정 완료")
    )
    @PatchMapping("/{reviewId}")
    @Idempotent
    public void updateReview(@AuthenticationPrincipal AuthenticatedAccount account,
                             @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                             @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                             @RequestBody @Validated UpdateReviewWebRequest request) {

        UpdateReviewRequest updateReviewRequest = new UpdateReviewRequest(reviewId, account.memberId(),
                request.updateReviewStarRating(), request.updateReviewContent(),
                (request.updateReviewImages() != null && !request.updateReviewImages().isEmpty()) || request.isUpdatedReviewImages(),
                request.updateReviewImages(), request.storageStrategy() != null ? request.storageStrategy() : StorageStrategy.CLOUD_STORAGE_API);

        reviewService.updateReview(updateReviewRequest);
    }

    @Operation(summary = "리뷰 댓글 수정", description = "리뷰 댓글 작성자가 댓글 또는 대댓글을 수정합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 댓글/대댓글 수정 완료")
    )
    @PatchMapping("/{reviewId}/comments/{commentId}")
    @Idempotent
    public void updateReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                    @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                    @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                    @Parameter(name = "리뷰 댓글/대댓글 아이디", in = ParameterIn.PATH) @PathVariable("commentId") Long commentId,
                                    @RequestBody @Validated UpdateReviewCommentWebRequest request) {

        reviewService.updateReviewComment(commentId, account.memberId(), request.updateReviewCommentContent());
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 작성자가 리뷰를 삭제합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 완료")
    )
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@AuthenticationPrincipal AuthenticatedAccount account,
                             @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId) {

        reviewService.deleteReview(reviewId, account.memberId());
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 댓글 작성자가 댓글을 삭제합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "리뷰 댓글 삭제 완료")
    )
    @DeleteMapping("/{reviewId}/comments/{commentId}")
    public void deleteReviewComment(@AuthenticationPrincipal AuthenticatedAccount account,
                                    @Parameter(name = "리뷰 아이디", in = ParameterIn.PATH) @PathVariable("reviewId") Long reviewId,
                                    @Parameter(name = "리뷰 댓글/대댓글 아이디", in = ParameterIn.PATH) @PathVariable("commentId") Long commentId) {

        reviewService.deleteReviewComment(commentId, account.memberId());
    }
}
