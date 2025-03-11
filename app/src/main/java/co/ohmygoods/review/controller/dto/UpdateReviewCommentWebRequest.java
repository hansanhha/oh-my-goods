package co.ohmygoods.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record UpdateReviewCommentWebRequest(

        @Schema(description = "수정된 리뷰 댓글 내용")
        @NotEmpty(message = "수정된 리뷰 댓글 내용을 입력해주세요")
        String updateReviewCommentContent) {
}
