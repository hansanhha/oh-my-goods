package co.ohmygoods.order.service;


import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.payment.model.event.PaymentCancelEvent;
import co.ohmygoods.payment.model.event.PaymentFailureEvent;
import co.ohmygoods.payment.model.event.PaymentSuccessEvent;


public interface OrderProcessingService {

    OrderCheckoutResponse checkout(OrderCheckoutRequest request);

    void successOrder(PaymentSuccessEvent event);

    void cancelOrderByPaymentCancellation(PaymentCancelEvent event);

    void failOrderByPaymentFailed(PaymentFailureEvent event);

}
