package co.ohmygoods.review.service.dto;


import co.ohmygoods.review.model.entity.Review;

import java.time.LocalDateTime;


public record ReviewResponse(String reviewerMemberId,
                             Long reviewId,
                             String reviewContent,
                             int reviewLikeCount,
                             int reviewStarRating,
                             LocalDateTime reviewWriteDate) {

    public static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getReviewer().getMemberId(), review.getId(),
                review.getContent(), review.getLike(), review.getStarRating(), review.getCreatedAt());
    }
}
