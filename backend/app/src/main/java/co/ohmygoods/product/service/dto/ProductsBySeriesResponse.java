package co.ohmygoods.product.service.dto;

import co.ohmygoods.product.model.entity.Product;
import org.springframework.data.domain.Page;

public record ProductsBySeriesResponse(Long shopId,
                                       ProductSeriesDto productSeries,
                                       Page<Product> products) {
}
