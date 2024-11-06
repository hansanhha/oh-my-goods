package co.ohmygoods.shop.business;

import co.ohmygoods.shop.business.entity.ShopOwnerChangeHistory;
import org.springframework.data.repository.CrudRepository;

public interface ShopOwnerChangeHistoryRepository extends CrudRepository<ShopOwnerChangeHistory, Long> {
}
