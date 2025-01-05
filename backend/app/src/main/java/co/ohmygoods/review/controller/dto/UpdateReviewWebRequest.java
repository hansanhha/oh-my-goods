package co.ohmygoods.review.controller.dto;

import co.ohmygoods.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateReviewWebRequest(Long  updateReviewId,
                                     String updateReviewContent,
                                     int updateReviewStarRating,
                                     boolean isUpdatedReviewImages,
                                     List<MultipartFile> updateReviewImages,
                                     StorageStrategy storageStrategy) {
}
