package co.ohmygoods.order.repository;

import co.ohmygoods.order.entity.Order;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);
}
