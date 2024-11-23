package co.ohmygoods.payment.repository;

import co.ohmygoods.payment.entity.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
