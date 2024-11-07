package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductFlexibleCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductDetailCategoryRepository extends CrudRepository<ProductFlexibleCategory, Long> {

    List<ProductFlexibleCategory> findAllByShop(Shop shop);
}
