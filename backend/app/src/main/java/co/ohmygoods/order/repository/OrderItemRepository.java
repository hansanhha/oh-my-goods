package co.ohmygoods.order.repository;

import co.ohmygoods.order.model.entity.OrderItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "JOIN FETCH oi.product " +
            "JOIN FETCH oi.order " +
            "WHERE oi.orderNumber = :orderNumber")
    Optional<OrderItem> fetchProductByOrderNumber(String orderNumber);
}