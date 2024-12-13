package co.ohmygoods.review.service.dto;

import co.ohmygoods.review.model.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(Long productId,
                             String reviewerEmail,
                             Long reviewId,
                             String reviewContent,
                             int reviewLikeCount,
                             int reviewStarRating,
                             LocalDateTime reviewWriteDate) {

    public static ReviewResponse from(Long productId, Review review) {
        return new ReviewResponse(productId, review.getReviewer().getEmail(), review.getId(),
                review.getContent(), review.getLike(), review.getStarRating(), review.getCreatedAt());
    }
}
