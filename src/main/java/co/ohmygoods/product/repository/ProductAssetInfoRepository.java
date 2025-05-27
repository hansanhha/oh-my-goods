package co.ohmygoods.product.repository;

import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.entity.ProductAsset;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductAssetInfoRepository extends CrudRepository<ProductAsset, Long> {
    List<ProductAsset> findByProduct(Product product);
}
