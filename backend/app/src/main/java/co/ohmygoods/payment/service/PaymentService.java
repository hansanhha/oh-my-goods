package co.ohmygoods.payment.service;

import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.vo.PaymentStatus;

import java.time.LocalDateTime;

public interface PaymentService {

    Long getPaymentId(String orderTransactionId);

    void createPayment(ExternalPaymentVendor externalPaymentVendor, String accountEmail, Long orderId, int paymentAmount, String paymentName);

    void readyPayment(String transactionId, LocalDateTime readyAt);

    void cancelPayment(String orderTransactionId, LocalDateTime canceledAt);

    void failPayment(String orderTransactionId, PaymentStatus paymentFailureCause, LocalDateTime failedAt);
}
