package co.ohmygoods.product.controller.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

public record ProductRegisterWebRequest(

        @Schema(description = "상품 유형")
        @NotEmpty(message = "올바르지 않은 상품 유형입니다")
        String productType,

        @Schema(description = "상품의 주요 카테고리")
        @NotEmpty(message = "올바르지 않은 상품 주요 카테고리입니다")
        String productMainCategory,

        @Schema(description = "상품의 서브 카테고리")
        @NotEmpty(message = "올바르지 않은 상품 하위 카테고리입니다")
        String productSubCategory,

        @Schema(description = "상점에서 등록한 커스텀 카테고리")
        List<Long> productCustomCategoryIds,

        @Schema(description = "상품 이름")
        @NotEmpty(message = "올바르지 않은 상품 이름입니다")
        String productName,

        @Schema(description = "상품 설명")
        @NotEmpty(message = "올바르지 않은 상품 설명입니다")
        String productDescription,

        @Schema(description = "상품 이미지")
        MultipartFile[] productImages,

        @Schema(description = "상품 개수")
        @NotNull(message = "올바르지 않은 상품 개수입니다") @Positive(message = "올바르지 않은 상품 개수입니다")
        int productQuantity,

        @Schema(description = "상품 가격")
        @NotNull(message = "올바르지 않은 상품 가격입니다") @Positive(message = "올바르지 않은 상품 가격입니다")
        int productPrice,

        @Schema(description = "상품 할인율")
        int productDiscountRate,

        @Schema(description = "계정 당 상품 최대 구매 개수")
        int productPurchaseLimitCount,

        @Schema(description = "상품 할인 시작 기간")
        LocalDateTime productDiscountStartDate,

        @Schema(description = "상품 할인 종료 기간")
        LocalDateTime productDiscountEndDate,

        @Schema(description = "상품 판매 시작 기간")
        LocalDateTime productExpectedSaleDate) {
}
