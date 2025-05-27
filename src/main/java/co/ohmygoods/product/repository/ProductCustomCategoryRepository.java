package co.ohmygoods.product.repository;


import co.ohmygoods.product.model.entity.CustomCategory;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ProductCustomCategoryRepository extends CrudRepository<CustomCategory, Long> {

    List<CustomCategory> findAllByShop(Shop shop);

    @Query("SELECT cc " +
            "FROM CustomCategory cc " +
            "JOIN cc.shop on cc.shop = :shop " +
            "WHERE cc.id in :ids")
    List<CustomCategory> findAllByIdAndShop(Iterable<Long> ids, Shop shop);

    boolean existsByShopAndName(Shop shop, String name);
}
