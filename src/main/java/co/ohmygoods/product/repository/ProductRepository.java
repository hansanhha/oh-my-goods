package co.ohmygoods.product.repository;


import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.model.entity.Shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface ProductRepository extends CrudRepository<Product, Long>,
        PagingAndSortingRepository<Product, Long>, ProductRepositoryCustom {

    Slice<Product> findAllByShop(Shop shop, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN Shop s " +
            "WHERE p.shop = :shop AND p.id IN :ids")
    List<Product> findAllByShopAndId(Shop shop, List<Long> ids);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop " +
            "WHERE p.id = :id")
    Optional<Product> fetchShopById(Long id);
}
