package co.ohmygoods.order.repository;

import co.ohmygoods.order.model.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {

    Optional<Order> fetchAccountByTransactionId(String orderTransactionId);

    @Query("SELECT o " +
            "FROM Order o " +
            "JOIN FETCH o.account " +
            "JOIN FETCH o.orderItems oi " +
            "JOIN FETCH Product p ON oi.product = p " +
            "WHERE o.id = :orderId")
    Optional<Order> fetchOrderItemsAndProductById(Long orderId);

    @Query("SELECT o FROM Order o JOIN o.payment p WHERE p.id = :paymentId")
    Optional<Order> findByPaymentId(Long paymentId);

    Optional<Order> findByAccountMemberId(String memberId, Pageable pageable);
}
