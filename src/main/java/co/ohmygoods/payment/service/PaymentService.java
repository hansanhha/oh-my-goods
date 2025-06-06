package co.ohmygoods.payment.service;

import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public interface PaymentService {

    Long createPayment(PaymentAPIProvider paymentAPIProvider, String accountEmail, Long orderId, int paymentAmount, String paymentName);

    Long readyPayment(Long paymentId, String paymentTransactionId, LocalDateTime readyAt);

    Long successPayment(String orderTransactionId, LocalDateTime succeededAt);

    Long cancelPayment(String orderTransactionId, LocalDateTime canceledAt);

    Long failPayment(String orderTransactionId, PaymentStatus paymentFailureCause, LocalDateTime failedAt);
}
