package co.ohmygoods.payment.repository;

import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.payment.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);

    @Query("SELECT p " +
            "FROM Payment p " +
            "JOIN p.order o " +
            "WHERE o.transactionId = :orderTransactionId")
    Optional<Payment> findByOrderTransactionId(String orderTransactionId);
}
