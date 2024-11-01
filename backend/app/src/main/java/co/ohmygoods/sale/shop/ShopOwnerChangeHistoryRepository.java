package co.ohmygoods.sale.shop;

import co.ohmygoods.sale.shop.entity.ShopOwnerChangeHistory;
import org.springframework.data.repository.CrudRepository;

public interface ShopOwnerChangeHistoryRepository extends CrudRepository<ShopOwnerChangeHistory, Long> {
}
