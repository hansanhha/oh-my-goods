package co.ohmygoods.product.seller.dto;

import co.ohmygoods.product.vo.ProductCategory;
import co.ohmygoods.product.vo.ProductType;

import java.util.List;

public record ProductMetadataModifyInfo(String accountEmail,
                                        Long shopId,
                                        Long modifyProductId,
                                        String modifyName,
                                        String modifyDescription,
                                        ProductType modifyType,
                                        ProductCategory modifyCategory,
                                        List<Long> modifyDetailCategoryIds,
                                        List<Long> modifySeriesIds) {
}
