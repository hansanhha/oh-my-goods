package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductGeneralCategory;
import co.ohmygoods.product.repository.dto.ProductShopDto;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductRepositoryCustom {

    Slice<ProductShopDto> fetchAllSalesProductByGeneralCategory(ProductGeneralCategory category, Pageable pageable);

    Slice<Product> fetchAllSalesProductByShopAndCategory(Shop shop, ProductGeneralCategory category, Pageable pageable);

    Slice<Product> fetchAllSalesProductByShopAndCustomCategory(Shop shop, ProductCustomCategory category, Pageable pageable);
}
