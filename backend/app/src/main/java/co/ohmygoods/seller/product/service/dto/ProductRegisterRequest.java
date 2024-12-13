package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductRegisterRequest(String accountEmail,
                                     Long shopId,
                                     ProductType type,
                                     ProductMainCategory category,
                                     ProductStockStatus status,
                                     List<Long> customCategoryIds,
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
