package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateProductMetadataRequest(String ownerMemberId,
                                           Long modifyProductId,
                                           String modifyName,
                                           String modifyDescription,
                                           ProductType modifyType,
                                           ProductMainCategory modifyTopCategory,
                                           List<Long> modifyCustomCategoryIds) {
}
