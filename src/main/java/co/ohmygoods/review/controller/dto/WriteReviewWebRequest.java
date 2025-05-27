package co.ohmygoods.review.controller.dto;

import co.ohmygoods.global.file.model.vo.StorageStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record WriteReviewWebRequest(
        @Schema(description = "리뷰를 작성할 주문 번호")
        @NotEmpty(message = "올바르지 않은 주문 번호입니다")
        String reviewOrderNumber,

        @Schema(description = "리뷰 내용")
        @NotEmpty(message = "리뷰 내용을 작성해주세요")
        String reviewContent,

        @Schema(description = "리뷰 평점")
        @NotNull(message = "올바르지 않은 평점입니다") @Positive(message = "올바르지 않은 평점입니다")
        int reviewStarRating,

        @Schema(description = "리뷰 이미지")
        List<MultipartFile> reviewImages,

        @Schema(description = "리뷰 이미지 저장 방식 지정. 프론트엔드에서 클라우드 접속 URL을 통해 업로드하거나 백엔드에서 클라우드에 직접 업로드합니다",
                examples = {"PROVIDE_CLOUD_STORAGE_ACCESS_URL", "CLOUD_STORAGE_API"})
        StorageStrategy storageStrategy) {
}
