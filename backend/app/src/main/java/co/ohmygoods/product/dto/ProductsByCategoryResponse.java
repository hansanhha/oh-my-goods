package co.ohmygoods.product.dto;

import co.ohmygoods.product.entity.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductsByCategoryResponse(Long shopId,
                                         String topCategory,
                                         List<ProductDetailCategoryDto> detailCategories,
                                         Page<Product> products) {
}
