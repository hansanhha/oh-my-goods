package co.ohmygoods.order.service;

import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.payment.model.event.PaymentCancelEvent;
import co.ohmygoods.payment.model.event.PaymentFailureEvent;
import co.ohmygoods.payment.model.event.PaymentSuccessEvent;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.util.UUID;

public interface OrderTransactionService {

    OrderCheckoutResponse checkout(OrderCheckoutRequest request);

    void successOrder(PaymentSuccessEvent event);

    void cancelOrderByPaymentCancellation(PaymentCancelEvent event);

    void failOrderByPaymentFailed(PaymentFailureEvent event);

    default String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }
}
