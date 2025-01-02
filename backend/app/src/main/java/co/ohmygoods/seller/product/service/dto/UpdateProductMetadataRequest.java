package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record UpdateProductMetadataRequest(String ownerMemberId,
                                           Long updateProductId,
                                           String updateName,
                                           String updateDescription,
                                           ProductType updateType,
                                           ProductMainCategory updateMainCategory,
                                           List<Long> updateCustomCategoryIds,
                                           MultipartFile[] updateAssets) {
}
