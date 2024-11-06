package co.ohmygoods.product.business.dto;

import co.ohmygoods.product.vo.ProductCategory;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.product.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductRegisterInfo(String accountEmail,
                                  Long shopId,
                                  ProductType type,
                                  ProductCategory category,
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
                                  boolean isImmediateSale,
                                  LocalDateTime expectedSaleDate) {
}
