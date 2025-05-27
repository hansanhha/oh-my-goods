package co.ohmygoods.product.service.user.dto;

import co.ohmygoods.product.model.entity.CustomCategory;


public record ProductCustomCategoryResponse(Long productCustomCategoryId,
                                            String productCustomCategoryName) {

    public static ProductCustomCategoryResponse from(CustomCategory customCategory) {
        return new ProductCustomCategoryResponse(customCategory.getId(), customCategory.getName());
    }
}
