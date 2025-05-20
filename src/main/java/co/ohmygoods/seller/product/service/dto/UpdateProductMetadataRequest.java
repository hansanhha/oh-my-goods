package co.ohmygoods.seller.product.service.dto;


import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.seller.product.controller.dto.UpdateProductMetadataWebRequest;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


public record UpdateProductMetadataRequest(String ownerMemberId,
                                           Long updateProductId,
                                           String updateName,
                                           String updateDescription,
                                           ProductType updateType,
                                           ProductMainCategory updateMainCategory,
                                           ProductSubCategory updateSubCategory,
                                           List<Long> updateCustomCategoryIds,
                                           MultipartFile[] updateAssets) {

    public static UpdateProductMetadataRequest of(String memberId, Long productId, UpdateProductMetadataWebRequest request) {
        return new UpdateProductMetadataRequest(
            memberId, 
            productId, 
            request.updateProductName(), 
            request.updateDescription(), 
            ProductType.valueOf(request.updateProductType()),
            ProductMainCategory.valueOf(request.updateProductMainCategory().toUpperCase()),
            ProductSubCategory.valueOf(request.updateProductSubCategory().toUpperCase()), 
            request.updateProductCustomCategoryIds(),
            request.updateProductImages());
    }
}
