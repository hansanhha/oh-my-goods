package co.ohmygoods.sale.product;

import co.ohmygoods.sale.product.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
