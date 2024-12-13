package co.ohmygoods.product.service.dto;

import co.ohmygoods.product.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

public record ProductsBySeriesResponse(Long shopId,
                                       ProductSeriesResponse productSeries,
                                       Slice<ProductResponse> products) {
}
