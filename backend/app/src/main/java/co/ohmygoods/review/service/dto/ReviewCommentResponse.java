package co.ohmygoods.review.service.dto;

import co.ohmygoods.review.model.entity.ReviewComment;

import java.time.LocalDateTime;

public record ReviewCommentResponse(Long reviewId,
                                    Long parentReviewCommentId,
                                    Long reviewCommentId,
                                    String reviewCommenterEmail,
                                    String reviewCommentContent,
                                    int reviewCommentLikeCount,
                                    LocalDateTime reviewCommentWriteDate) {

    public static ReviewCommentResponse from(Long reviewId, Long parentReviewCommentId, ReviewComment reviewComment) {
        return new ReviewCommentResponse(reviewId, parentReviewCommentId, reviewComment.getId(),
                reviewComment.getAccount().getEmail(), reviewComment.getContent(),
                reviewComment.getLike(), reviewComment.getCreatedAt());
    }
}
