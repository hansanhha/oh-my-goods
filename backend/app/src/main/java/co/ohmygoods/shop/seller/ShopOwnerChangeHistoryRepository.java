package co.ohmygoods.shop.seller;

import co.ohmygoods.shop.seller.entity.ShopOwnerChangeHistory;
import org.springframework.data.repository.CrudRepository;

public interface ShopOwnerChangeHistoryRepository extends CrudRepository<ShopOwnerChangeHistory, Long> {
}
