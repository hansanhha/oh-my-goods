package co.ohmygoods.product.service.dto;

import co.ohmygoods.product.model.entity.ProductCustomCategory;

public record ProductCustomCategoryResponse(Long productCustomCategoryId,
                                            String productCustomCategoryName) {

    public static ProductCustomCategoryResponse from(ProductCustomCategory customCategory) {
        return new ProductCustomCategoryResponse(customCategory.getId(), customCategory.getCustomCategoryName());
    }
}
