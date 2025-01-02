package co.ohmygoods.seller.product.web.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record UpdateProductMetadataWebRequest(String updateProductName,
                                              String updateDescription,
                                              String updateProductType,
                                              String updateProductMainCategory,
                                              String updateProductSubCategory,
                                              List<Long> updateProductCustomCategoryIds,
                                              MultipartFile[] updateProductImages) {
}
