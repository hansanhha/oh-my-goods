package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.Product;
import co.ohmygoods.product.entity.ProductDetailCategory;
import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.product.vo.ProductTopCategory;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductSeriesMapping psm on p = psm.product AND psm.series = :series " +
            "WHERE p.shop =: shop")
    List<Product> findAllByShopAndSeries(Shop shop, ProductSeries series);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductSeriesMapping psm ON p = psm.product AND psm.series = :series " +
            "WHERE p.shop = :shop")
    Page<Product> findAllByShopAndSeries(Shop shop, ProductSeries series, Pageable pageable);

    Page<Product> findAllByShop(Shop shop, Pageable pageable);

    Page<Product> findAllByShopAndTopCategory(Shop shop, ProductTopCategory topCategory, Pageable pageable);

    @Query("SELECT p " +
            "FROM Product p " +
            "JOIN ProductDetailCategoryMapping pcm ON p= pcm.product AND pcm.detailCategory = :detailCategory " +
            "WHERE p.shop = :shop")
    Page<Product> findAllByShopAndDetailCategory(Shop shop, ProductDetailCategory detailCategory, Pageable pageable);
}
