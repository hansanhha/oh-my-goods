package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record SellerProductResponse(Long shopId,
                                    Long productId,
                                    String productName,
                                    String productDescription,
                                    ProductType productType,
                                    ProductMainCategory productMainCategory,
                                    String productSubCategory,
                                    ProductStockStatus productStockStatus,
                                    List<CustomCategoryResponse> productCustomCategories,
                                    int productQuantity,
                                    int productPurchaseLimit,
                                    int productPrice,
                                    int productDiscountRate,
                                    LocalDateTime productDiscountStartDate,
                                    LocalDateTime productDiscountEndDate,
                                    LocalDateTime productRegisteredAt,
                                    LocalDateTime productLastModifiedAt) {
}
