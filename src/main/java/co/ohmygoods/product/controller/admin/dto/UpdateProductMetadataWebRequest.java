package co.ohmygoods.product.controller.admin.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.product.service.admin.dto.UpdateProductMetadataRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateProductMetadataWebRequest(

        @Schema(description = "수정된 상품 이름")
        @NotEmpty(message = "올바르지 않은 상품 이름입니다")
        String updateProductName,

        @Schema(description = "수정된 상품 설명")
        @NotEmpty(message = "올바르지 않은 상품 설명입니다")
        String updateDescription,

        @Schema(description = "수정된 상품 유형")
        @NotEmpty(message = "올바르지 않은 상품 유형입니다")
        String updateProductType,

        @Schema(description = "수정된 상품 주요 카테고리")
        @NotEmpty(message = "올바르지 않은 상품 주요 카테고리입니다")
        String updateProductMainCategory,

        @Schema(description = "수정된 상품 서브 카테고리")
        @NotEmpty(message = "올바르지 않은 상품 하위 카테고리입니다")
        String updateProductSubCategory,

        @Schema(description = "수정된 상품 커스텀 카테고리")
        List<Long> updateProductCustomCategoryIds,

        @Schema(description = "수정된 상품 이미지")
        MultipartFile[] updateProductImages) {

        public UpdateProductMetadataRequest toServiceDto(String memberId, Long productId) {
                return new UpdateProductMetadataRequest(
                        memberId,
                        productId,
                        updateProductName(),
                        updateDescription(),
                        ProductType.valueOf(updateProductType()),
                        ProductMainCategory.valueOf(updateProductMainCategory().toUpperCase()),
                        ProductSubCategory.valueOf(updateProductSubCategory().toUpperCase()),
                        updateProductCustomCategoryIds(),
                        updateProductImages());
        }

}
