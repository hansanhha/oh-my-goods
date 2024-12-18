package co.ohmygoods.order.service;

import co.ohmygoods.order.service.dto.OrderStartRequest;
import co.ohmygoods.order.service.dto.OrderStartResponse;
import co.ohmygoods.payment.vo.PaymentStatus;

import java.util.UUID;

public interface OrderService {

    OrderStartResponse startOrder(OrderStartRequest request);

    void successOrder(Long orderId);

    void cancelOrderByPaymentCancellation(Long orderId);

    void failOrderByPaymentFailed(Long orderId, PaymentStatus paymentFailureCause);

    default String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }
}
