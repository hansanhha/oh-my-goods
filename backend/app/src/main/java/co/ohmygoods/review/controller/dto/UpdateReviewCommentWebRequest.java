package co.ohmygoods.review.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record UpdateReviewCommentWebRequest(@NotEmpty String updateReviewCommentContent) {
}
