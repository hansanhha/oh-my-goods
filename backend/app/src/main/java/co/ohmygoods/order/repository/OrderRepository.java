package co.ohmygoods.order.repository;

import co.ohmygoods.order.entity.Order;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
}
