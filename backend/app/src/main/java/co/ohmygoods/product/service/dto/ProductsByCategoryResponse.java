package co.ohmygoods.product.service.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record ProductsByCategoryResponse(Long shopId,
                                         String topCategory,
                                         List<ProductCustomCategoryResponse> detailCategories,
                                         Slice<ProductResponse> products) {
}
