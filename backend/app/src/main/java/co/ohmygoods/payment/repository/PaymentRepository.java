package co.ohmygoods.payment.repository;

import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.payment.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.order po JOIN FETCH p.order.product")
    Optional<Payment> findFetchOrderAndProductByTransactionId(String transactionId);

    Optional<Payment> findByOrder(Order order);
}
