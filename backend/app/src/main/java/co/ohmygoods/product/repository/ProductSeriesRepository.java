package co.ohmygoods.product.repository;

import co.ohmygoods.product.entity.ProductSeries;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {

    List<ProductSeries> findAllByShop(Shop shop);
}
