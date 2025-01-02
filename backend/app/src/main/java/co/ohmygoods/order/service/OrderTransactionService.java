package co.ohmygoods.order.service;

import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.util.UUID;

public interface OrderTransactionService {

    OrderCheckoutResponse checkout(OrderCheckoutRequest request);

    void successOrder(Long orderId);

    void cancelOrderByPaymentCancellation(Long orderId);

    void failOrderByPaymentFailed(Long orderId, PaymentStatus paymentFailureCause);

    default String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }
}
