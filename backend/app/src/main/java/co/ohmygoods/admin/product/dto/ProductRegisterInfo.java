package co.ohmygoods.admin.product.dto;

import co.ohmygoods.domain.product.vo.ProductCategory;
import co.ohmygoods.domain.product.vo.ProductStatus;
import co.ohmygoods.domain.product.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductRegisterInfo(String accountEmail,
                                  Long shopId,
                                  ProductType type,
                                  ProductCategory category,
                                  ProductStatus status,
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
