package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductMetadataModifyInfo(String accountEmail,
                                        Long shopId,
                                        Long modifyProductId,
                                        String modifyName,
                                        String modifyDescription,
                                        ProductType modifyType,
                                        ProductMainCategory modifyTopCategory,
                                        List<Long> modifyCustomCategoryIds,
                                        List<Long> modifySeriesIds) {
}
