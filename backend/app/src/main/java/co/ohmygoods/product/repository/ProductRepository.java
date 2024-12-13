package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductCustomCategory;
import co.ohmygoods.product.model.entity.ProductSeries;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductSeriesMapping psm on p = psm.product AND psm.series = :series " +
            "WHERE p.shop = :shop")
    List<Product> findAllByShopAndSeries(Shop shop, ProductSeries series);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductSeriesMapping psm ON p = psm.product AND psm.series = :series " +
            "WHERE p.shop = :shop")
    Slice<Product> findAllByShopAndSeries(Shop shop, ProductSeries series, Pageable pageable);

    Slice<Product> findAllByShop(Shop shop, Pageable pageable);

    Slice<Product> findAllByShopAndTopCategory(Shop shop, ProductMainCategory topCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductCustomCategoryMapping pcm ON p= pcm.product AND pcm.customCategory = :detailCategory " +
            "WHERE p.shop = :shop")
    Slice<Product> findAllByShopAndDetailCategory(Shop shop, ProductCustomCategory detailCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN Shop s ON p.shop = :shop " +
            "WHERE p.id IN :ids")
    List<Product> findAllByShopAndId(Shop shop, List<Long> ids);
}
