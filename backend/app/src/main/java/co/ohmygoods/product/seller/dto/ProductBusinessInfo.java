package co.ohmygoods.product.seller.dto;

import co.ohmygoods.product.vo.ProductFixedCategory;
import co.ohmygoods.product.vo.ProductStockStatus;
import co.ohmygoods.product.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductBusinessInfo(Long shopId,
                                  Long productId,
                                  String name,
                                  String description,
                                  ProductType type,
                                  ProductFixedCategory category,
                                  ProductStockStatus stockStatus,
                                  List<String> series,
                                  List<String> detailCategory,
                                  int quantity,
                                  int purchaseLimit,
                                  int price,
                                  int discountRate,
                                  LocalDateTime discountStartDate,
                                  LocalDateTime discountEndDate) {
}
