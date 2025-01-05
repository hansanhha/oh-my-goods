package co.ohmygoods.review.controller.dto;

import co.ohmygoods.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record WriteReviewWebRequest(String reviewOrderNumber,
                                    String reviewContent,
                                    int reviewStarRating,
                                    List<MultipartFile> reviewImages,
                                    StorageStrategy storageStrategy) {
}
