package co.ohmygoods.product.dto;

import co.ohmygoods.product.entity.Product;
import org.springframework.data.domain.Page;

public record ProductsBySeriesResponse(Long shopId,
                                       ProductSeriesDto productSeries,
                                       Page<Product> products) {
}
