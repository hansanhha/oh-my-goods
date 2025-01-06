package co.ohmygoods.seller.product.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

public record RegisterProductWebRequest(@NotEmpty(message = "올바르지 않은 상품 유형입니다")
                                        String productType,
                                        @NotEmpty(message = "올바르지 않은 상품 주요 카테고리입니다")
                                        String productMainCategory,
                                        @NotEmpty(message = "올바르지 않은 상품 하위 카테고리입니다")
                                        String productSubCategory,
                                        @NotEmpty(message = "올바르지 않은 상품 상태입니다")
                                        String productStatus,
                                        List<Long> productCustomCategoryIds,
                                        @NotEmpty(message = "올바르지 않은 상품 이름입니다")
                                        String productName,
                                        @NotEmpty(message = "올바르지 않은 상품 설명입니다")
                                        String productDescription,
                                        MultipartFile[] productImages,
                                        @NotNull(message = "올바르지 않은 상품 개수입니다") @Positive(message = "올바르지 않은 상품 개수입니다")
                                        int productQuantity,
                                        @NotNull(message = "올바르지 않은 상품 가격입니다") @Positive(message = "올바르지 않은 상품 가격입니다")
                                        int productPrice,
                                        int productDiscountRate,
                                        int productPurchaseLimitCount,
                                        LocalDateTime productDiscountStartDate,
                                        LocalDateTime productDiscountEndDate,
                                        LocalDateTime productExpectedSaleDate) {
}
