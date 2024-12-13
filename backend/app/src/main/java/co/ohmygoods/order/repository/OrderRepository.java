package co.ohmygoods.order.repository;

import co.ohmygoods.order.model.entity.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o " +
            "FROM Order o " +
            "JOIN FETCH o.product " +
            "WHERE o.orderNumber = :orderNumber")
    Optional<Order> fetchProductByOrderNumber(String orderNumber);
}
