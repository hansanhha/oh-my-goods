package co.ohmygoods.shop.service.dto;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.model.vo.ShopStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record ShopOverviewResponse(String name,
                                   String introduction,
                                   LocalDateTime createdAt,
                                   String shopImage,
                                   ShopStatus status,
                                   Map<Long, String> shopAllProductSeries,
                                   Map<Long, String> shopAllProductCategories,
                                   List<Product> products) {
}
