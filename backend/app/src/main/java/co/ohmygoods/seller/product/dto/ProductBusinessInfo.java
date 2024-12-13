package co.ohmygoods.seller.product.dto;

import co.ohmygoods.product.model.vo.ProductTopCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProductBusinessInfo(Long shopId,
                                  Long productId,
                                  String name,
                                  String description,
                                  ProductType type,
                                  ProductTopCategory category,
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
