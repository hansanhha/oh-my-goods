package co.ohmygoods.seller.product.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateProductMetadataWebRequest(@NotEmpty(message = "올바르지 않은 상품 이름입니다")
                                              String updateProductName,
                                              @NotEmpty(message = "올바르지 않은 상품 설명입니다")
                                              String updateDescription,
                                              @NotEmpty(message = "올바르지 않은 상품 유형입니다")
                                              String updateProductType,
                                              @NotEmpty(message = "올바르지 않은 상품 주요 카테고리입니다")
                                              String updateProductMainCategory,
                                              @NotEmpty(message = "올바르지 않은 상품 하위 카테고리입니다")
                                              String updateProductSubCategory,
                                              List<Long> updateProductCustomCategoryIds,
                                              MultipartFile[] updateProductImages) {
}
