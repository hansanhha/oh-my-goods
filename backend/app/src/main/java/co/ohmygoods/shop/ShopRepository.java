package co.ohmygoods.shop;

import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ShopRepository extends CrudRepository<Shop, Long> {

    Optional<Shop> findByName(String name);
}
