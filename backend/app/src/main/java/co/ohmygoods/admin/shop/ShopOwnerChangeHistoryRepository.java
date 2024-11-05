package co.ohmygoods.admin.shop;

import co.ohmygoods.domain.shop.entity.ShopOwnerChangeHistory;
import org.springframework.data.repository.CrudRepository;

public interface ShopOwnerChangeHistoryRepository extends CrudRepository<ShopOwnerChangeHistory, Long> {
}
