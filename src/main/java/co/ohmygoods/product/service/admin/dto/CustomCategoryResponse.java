package co.ohmygoods.product.service.admin.dto;

import co.ohmygoods.product.model.entity.ProductCustomCategory;

public record CustomCategoryResponse(Long customCategoryId,
                                     String customCategoryName) {

    public static CustomCategoryResponse from(ProductCustomCategory customCategory) {
        return new CustomCategoryResponse(customCategory.getId(), customCategory.getCustomCategoryName());
    }
}
