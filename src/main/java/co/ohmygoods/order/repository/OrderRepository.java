package co.ohmygoods.order.repository;


import co.ohmygoods.order.model.entity.Order;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o " +
            "FROM Order o " +
            "JOIN FETCH Account " +
            "WHERE o.transactionId = :transactionId")
    Optional<Order> fetchAccountByTransactionId(String transactionId);


    @Query("SELECT o " +
            "FROM Order o " +
            "JOIN FETCH o.orderItems " +
            "JOIN o.payment p ON p.id = :paymentId")
    Optional<Order> fetchOrderItemsByPaymentId(Long paymentId);

}
