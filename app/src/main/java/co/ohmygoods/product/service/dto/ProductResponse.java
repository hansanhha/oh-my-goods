package co.ohmygoods.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductResponse(Long shopId,
                              String shopName,
                              Long productId,
                              String productName,
                              String productDescription,
                              ProductType productType,
                              ProductMainCategory productMainCategory,
                              ProductSubCategory productSubCategory,
                              ProductStockStatus productStockStatus,
                              List<ProductCustomCategoryResponse> productCustomCategories,
                              int productQuantity,
                              int productPurchaseLimit,
                              int productPrice,
                              int productDiscountRate,
                              LocalDateTime productDiscountStartDate,
                              LocalDateTime productDiscountEndDate,
                              LocalDateTime productRegisteredAt) {

}
