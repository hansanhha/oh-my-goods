package co.ohmygoods.review.service.dto;

import co.ohmygoods.global.file.model.vo.StorageStrategy;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateReviewRequest(Long  updateReviewId,
                                  String memberId,
                                  int reviewStarRating,
                                  String updateReviewContent,
                                  boolean isUpdatedReviewImages,
                                  List<MultipartFile> updateReviewImages,
                                  StorageStrategy storageStrategy) {
}
