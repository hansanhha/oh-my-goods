package co.ohmygoods.product.seller.dto;

import co.ohmygoods.product.vo.ProductTopCategory;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.product.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductRegisterRequest(String accountEmail,
                                     Long shopId,
                                     ProductType type,
                                     ProductTopCategory category,
                                     ProductStockStatus status,
                                     List<Long> detailCategoryIds,
                                     List<Long> seriesIds,
                                     String name,
                                     int quantity,
                                     int price,
                                     int discountRate,
                                     LocalDateTime discountStartDate,
                                     LocalDateTime discountEndDate,
                                     int purchaseLimitCount,
                                     String description,
                                     LocalDateTime expectedSaleDate) {
}
