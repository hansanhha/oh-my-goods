package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;

import java.time.LocalDateTime;
import java.util.List;


public record ShopProductResponse(Long shopId,
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

    public static ShopProductResponse of(Long shopId, Product product, List<CustomCategoryResponse> customCategories) {
        return new ShopProductResponse(
            shopId, 
            product.getId(),
            product.getName(),
            product.getDescription(), 
            product.getType(),
            product.getCategory().getMainCategory(),
            product.getCategory().getSubCategory(),
            product.getStockStatus(),
            customCategories,
            product.getRemainingQuantity(),
            product.getPurchaseMaximumQuantity(),
            product.getOriginalPrice(),
            product.getDiscountRate(),
            product.getDiscountStartDate(),
            product.getDiscountEndDate(),
            product.getCreatedAt(),
            product.getLastModifiedAt());
    }
}
