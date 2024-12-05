package co.ohmygoods.review.service.dto;

public record WriteReviewRequest(String reviewOrderNumber,
                                 String accountEmail,
                                 String reviewContent,
                                 int reviewStarRating) {
}
