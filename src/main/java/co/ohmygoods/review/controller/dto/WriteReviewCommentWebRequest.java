package co.ohmygoods.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record WriteReviewCommentWebRequest(
        @Schema(description = "리뷰 댓글 내용")
        @NotEmpty(message = "리뷰 댓글 내용을 작성해주세요")
        String reviewCommentContent) {
}
