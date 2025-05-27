package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;

import java.time.LocalDateTime;
import java.util.List;


public record ProductAdminResponse(Long shopId,
                                   Long productId,
                                   String productName,
                                   String productDescription,
                                   ProductType productType,
                                   ProductMainCategory productMainCategory,
                                   ProductSubCategory productSubCategory,
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
