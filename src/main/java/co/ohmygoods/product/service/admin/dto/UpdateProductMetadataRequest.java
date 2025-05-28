package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;

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
                                           List<MultipartFile> updateAssets) {

}
