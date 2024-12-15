package co.ohmygoods.payment.repository;

import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.payment.entity.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.order po JOIN FETCH p.order.account")
    Optional<Payment> findFetchOrderAndAccountByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p JOIN FETCH p.order po JOIN FETCH p.order.account JOIN FETCH p.order.product")
    Optional<Payment> fetchByOrderWithOrderAndAccountAndProduct(OrderItem orderItem);

    @Query("SELECT p FROM Payment p JOIN FETCH p.order po JOIN FETCH p.order.product")
    Optional<Payment> findFetchOrderAndProductByTransactionId(String transactionId);
}
