package co.ohmygoods.order.repository;

import co.ohmygoods.order.model.entity.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "JOIN oi.product " +
            "JOIN oi.order " +
            "JOIN FETCH oi.deliveryAddress " +
            "WHERE oi.id = :orderItemId")
    Slice<OrderItem> findAllByOrderAccountMemberId(String memberId, Pageable pageable);

    @Query("SELECT oi " +
            "FROM OrderItem oi " +
            "JOIN FETCH oi.product " +
            "JOIN FETCH oi.deliveryAddress " +
            "WHERE oi.id = :orderItemId")
    Optional<OrderItem> fetchById(Long orderItemId);
}
