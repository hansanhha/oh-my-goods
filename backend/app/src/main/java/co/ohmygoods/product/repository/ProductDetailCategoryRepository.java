package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductDetailCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductDetailCategoryRepository extends CrudRepository<ProductDetailCategory, Long> {

    List<ProductDetailCategory> findAllByShop(Shop shop);

    @Query("SELECT pdc " +
            "FROM ProductDetailCategory pdc " +
            "JOIN pdc.shop on pdc.shop = :shop " +
            "WHERE pdc.topCategory = :topCategoryName")
    List<ProductDetailCategory> findAllByShopAndTopCategoryName(Shop shop, String topCategoryName);

    Optional<ProductDetailCategory> findByCategoryName(String categoryName);

    @Query("SELECT pdc " +
            "FROM ProductDetailCategory pdc " +
            "JOIN pdc.shop on pdc.shop = :shop " +
            "WHERE pdc.id in :ids")
    List<ProductDetailCategory> findAllByIdAndShop(Iterable<Long> ids, Shop shop);
}
