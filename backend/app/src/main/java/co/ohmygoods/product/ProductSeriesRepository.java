package co.ohmygoods.product;

import co.ohmygoods.product.entity.ProductSeries;
import org.springframework.data.repository.CrudRepository;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {
}
