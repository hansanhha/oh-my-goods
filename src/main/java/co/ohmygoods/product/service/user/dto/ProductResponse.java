package co.ohmygoods.product.service.user.dto;


import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;

import java.time.LocalDateTime;
import java.util.List;


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

    public static ProductResponse of(Long shopId, String shopName, Product product, List<ProductCustomCategoryResponse> customCategories) {
        return new ProductResponse(
            shopId, 
            shopName, 
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
            product.getCreatedAt());
    }

}
