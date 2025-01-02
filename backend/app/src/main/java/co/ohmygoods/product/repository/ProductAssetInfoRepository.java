package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductAssetInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductAssetInfoRepository extends CrudRepository<ProductAssetInfo, Long> {
    List<ProductAssetInfo> findByProduct(Product product);
}
