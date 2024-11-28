package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {

    List<ProductSeries> findAllByShop(Shop shop);

    Optional<ProductSeries> findBySeriesName(String seriesName);

    @Query("SELECT ps " +
            "FROM ProductSeries ps " +
            "JOIN ps.shop on ps.shop = :shop " +
            "WHERE ps.id in :ids")
    List<ProductSeries> findAllByIdAndShop(Iterable<Long> ids, Shop shop);
}
