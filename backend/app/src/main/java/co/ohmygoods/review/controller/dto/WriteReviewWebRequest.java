package co.ohmygoods.review.controller.dto;

import co.ohmygoods.file.model.vo.StorageStrategy;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record WriteReviewWebRequest(@NotEmpty(message = "올바르지 않은 주문 번호입니다")
                                    String reviewOrderNumber,
                                    @NotEmpty(message = "리뷰 내용을 작성해주세요")
                                    String reviewContent,
                                    @NotNull(message = "올바르지 않은 평점입니다") @Positive(message = "올바르지 않은 평점입니다")
                                    int reviewStarRating,
                                    List<MultipartFile> reviewImages,
                                    StorageStrategy storageStrategy) {
}
