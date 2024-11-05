package co.ohmygoods.product;

import co.ohmygoods.domain.product.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
