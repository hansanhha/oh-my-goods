package co.ohmygoods.product.seller.dto;

import co.ohmygoods.product.vo.ProductTopCategory;
import co.ohmygoods.product.vo.ProductType;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductMetadataModifyInfo(String accountEmail,
                                        Long shopId,
                                        Long modifyProductId,
                                        String modifyName,
                                        String modifyDescription,
                                        ProductType modifyType,
                                        ProductTopCategory modifyTopCategory,
                                        List<Long> modifyDetailCategoryIds,
                                        List<Long> modifySeriesIds) {
}
