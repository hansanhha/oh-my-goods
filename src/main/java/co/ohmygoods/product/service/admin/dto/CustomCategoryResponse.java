package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.entity.CustomCategory;


public record CustomCategoryResponse(Long customCategoryId,
                                     String customCategoryName) {

    public static CustomCategoryResponse from(CustomCategory customCategory) {
        return new CustomCategoryResponse(customCategory.getId(), customCategory.getName());
    }
}
