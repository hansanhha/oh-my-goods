package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
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
                                  ProductMainCategory category,
                                  ProductStockStatus stockStatus,
                                  List<String> series,
                                  List<String> customCategories,
                                  int quantity,
                                  int purchaseLimit,
                                  int price,
                                  int discountRate,
                                  LocalDateTime discountStartDate,
                                  LocalDateTime discountEndDate) {
}
