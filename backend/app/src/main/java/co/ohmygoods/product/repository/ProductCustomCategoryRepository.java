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
            "WHERE pdc.customCategoryName = :customCategoryName")
    List<ProductCustomCategory> findAllByShopAndCustomCategoryName(Shop shop, String customCategoryName);

    Optional<ProductCustomCategory> findByCustomCategoryName(String customCategoryName);

    @Query("SELECT pdc " +
            "FROM ProductCustomCategory pdc " +
            "JOIN pdc.shop on pdc.shop = :shop " +
            "WHERE pdc.id in :ids")
    List<ProductCustomCategory> findAllByIdAndShop(Iterable<Long> ids, Shop shop);
}
