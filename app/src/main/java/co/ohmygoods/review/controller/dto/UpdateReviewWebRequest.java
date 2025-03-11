package co.ohmygoods.review.controller.dto;

import co.ohmygoods.file.model.vo.StorageStrategy;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateReviewWebRequest(

        @Schema(description = "수정된 리뷰 내용")
        @NotEmpty(message = "수정된 리뷰 내용을 작성해주세요")
        String updateReviewContent,

        @Schema(description = "수정된 리뷰 평점")
        @NotNull(message = "수정된 리뷰 평점을 입력해주세요") @Positive(message = "리뷰 평점을 1보다 작을 수 없습니다")
        int updateReviewStarRating,

        @Schema(description = "리뷰 이미지 수정 여부")
        Boolean isUpdatedReviewImages,

        @Schema(description = "수정된 리뷰 이미지")
        List<MultipartFile> updateReviewImages,

        @Schema(description = "리뷰 이미지 저장 방식 지정. 프론트엔드에서 클라우드 접속 URL을 통해 업로드하거나 백엔드에서 클라우드에 직접 업로드합니다",
                examples = {"PROVIDE_CLOUD_STORAGE_ACCESS_URL", "CLOUD_STORAGE_API"})
        StorageStrategy storageStrategy) {
}
