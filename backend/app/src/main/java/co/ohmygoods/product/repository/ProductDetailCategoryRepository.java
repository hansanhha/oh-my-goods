package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductDetailCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDetailCategoryRepository extends CrudRepository<ProductDetailCategory, Long> {

    List<ProductDetailCategory> findAllByShop(Shop shop);

    List<ProductDetailCategory> findAllByShopAndTopCategoryName(Shop shop, String topCategoryName);

    Optional<ProductDetailCategory> findByCategoryName(String categoryName);
}
