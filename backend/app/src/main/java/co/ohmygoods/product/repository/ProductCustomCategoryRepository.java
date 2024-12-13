package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCustomCategoryRepository extends CrudRepository<ProductCustomCategory, Long> {

    List<ProductCustomCategory> findAllByShop(Shop shop);

    @Query("SELECT pdc " +
            "FROM ProductCustomCategory pdc " +
            "JOIN pdc.shop on pdc.shop = :shop " +
            "WHERE pdc.topCategory = :topCategoryName")
    List<ProductCustomCategory> findAllByShopAndTopCategoryName(Shop shop, String topCategoryName);

    Optional<ProductCustomCategory> findByCategoryName(String categoryName);

    @Query("SELECT pdc " +
            "FROM ProductCustomCategory pdc " +
            "JOIN pdc.shop on pdc.shop = :shop " +
            "WHERE pdc.id in :ids")
    List<ProductCustomCategory> findAllByIdAndShop(Iterable<Long> ids, Shop shop);
}
