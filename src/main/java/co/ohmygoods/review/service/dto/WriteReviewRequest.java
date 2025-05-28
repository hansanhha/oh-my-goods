package co.ohmygoods.review.service.dto;

import co.ohmygoods.global.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record WriteReviewRequest(String reviewOrderNumber,
                                 String memberId,
                                 String reviewContent,
                                 int reviewStarRating,
                                 List<MultipartFile> images,
                                 StorageStrategy storageStrategy) {
}
