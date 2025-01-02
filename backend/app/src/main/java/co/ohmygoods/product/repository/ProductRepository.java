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

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    Slice<Product> findAllByShop(Shop shop, Pageable pageable);


    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop " +
            "JOIN FETCH p.customCategoriesMappings " +
            "WHERE p.mainCategory = :mainCategory " +
            "AND p.stockStatus = 'ON_SALES' ")
    Slice<Product> fetchAllByMainCategoryAndStockStatusOnSales(ProductMainCategory mainCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop ON p.shop = :shop " +
            "JOIN FETCH p.customCategoriesMappings " +
            "WHERE p.mainCategory = :mainCategory " +
            "AND p.stockStatus = 'ON_SALES' ")
    Slice<Product> fetchAllByShopAndMainCategoryAndStockStatusOnSales(Shop shop, ProductMainCategory mainCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop ON p.shop = :shop " +
            "JOIN FETCH p.customCategoriesMappings " +
            "WHERE p.subCategory = :subCategory " +
            "AND p.stockStatus = 'ON_SALES' ")
    Slice<Product> fetchAllByShopAndSubCategoryAndStockStatusOnSales(Shop shop, String subCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop ON p.shop = :shop " +
            "JOIN FETCH p.customCategoriesMappings " +
            "WHERE ProductCustomCategoryMapping.customCategory = :customCategory ")
    Slice<Product> fetchAllByShopAndCustomCategoryAndStockStatusOnSales(Shop shop, ProductCustomCategory customCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN FETCH p.shop ON p.shop = :shop " +
            "JOIN FETCH p.customCategoriesMappings " +
            "WHERE p.stockStatus = 'ON_SALES' ")
    Slice<Product> fetchAllByShopAndStockStatusOnSales(Shop shop, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN Shop s ON p.shop = :shop " +
            "WHERE p.id IN :ids")
    List<Product> findAllByShopAndId(Shop shop, List<Long> ids);
}
