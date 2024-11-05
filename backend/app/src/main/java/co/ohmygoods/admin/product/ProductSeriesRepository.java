package co.ohmygoods.admin.product;

import co.ohmygoods.domain.product.entity.ProductSeries;
import org.springframework.data.repository.CrudRepository;

public interface ProductSeriesRepository extends CrudRepository<ProductSeries, Long> {
}
