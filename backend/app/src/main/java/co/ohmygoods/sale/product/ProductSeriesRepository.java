package co.ohmygoods.sale.product;

import co.ohmygoods.sale.product.entity.ProductSeries;
import org.springframework.data.repository.CrudRepository;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {
}
