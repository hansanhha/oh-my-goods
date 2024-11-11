package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {

    List<ProductSeries> findAllByShop(Shop shop);

    Optional<ProductSeries> findBySeriesName(String seriesName);

    List<ProductSeries> findAllByIdAndShop(Iterable<Long> ids, Shop shop);
}
