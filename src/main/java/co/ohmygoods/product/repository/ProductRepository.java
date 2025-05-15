package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>,
        PagingAndSortingRepository<Product, Long>, ProductRepositoryCustom {

    Slice<Product> findAllByShop(Shop shop, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN Shop s " +
            "WHERE p.shop = :shop AND p.id IN :ids")
    List<Product> findAllByShopAndId(Shop shop, List<Long> ids);
}
