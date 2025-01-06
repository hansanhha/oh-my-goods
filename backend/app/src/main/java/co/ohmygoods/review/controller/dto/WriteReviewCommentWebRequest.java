package co.ohmygoods.review.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record WriteReviewCommentWebRequest(@NotEmpty String reviewCommentContent) {
}
