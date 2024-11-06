package co.ohmygoods.product;

import co.ohmygoods.product.entity.Product;
import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long>, PagingAndSortingRepository<Product, Long> {

    @Query("SELECT p FROM Product p " +
            "JOIN ProductSeriesMapping psm ON p.id = psm.product.id " +
            "JOIN ProductSeries ps ON psm.productSeries.id = ps.id " +
            "WHERE ps.id = :seriesId")
    List<Product> findAllBySeries(Long seriesId);

    Page<Product> findAllByShop(Shop shop, Pageable pageable);
}
