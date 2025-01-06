package co.ohmygoods.review.controller.dto;

import co.ohmygoods.file.model.vo.StorageStrategy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateReviewWebRequest(@NotEmpty String updateReviewContent,
                                     @NotNull @Positive int updateReviewStarRating,
                                     Boolean isUpdatedReviewImages,
                                     List<MultipartFile> updateReviewImages,
                                     StorageStrategy storageStrategy) {
}
