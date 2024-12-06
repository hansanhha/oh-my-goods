package co.ohmygoods.review.service.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public record WriteReviewRequest(String reviewOrderNumber,
                                 String accountEmail,
                                 String reviewContent,
                                 int reviewStarRating,
                                 Collection<? extends MultipartFile> reviewImages) {
}
