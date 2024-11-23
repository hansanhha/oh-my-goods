package co.ohmygoods.payment.exception;

import co.ohmygoods.order.vo.OrderStatus;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public static void throwCauseInvalidPaymentPrice(int price) {
        throw new PaymentException(String.valueOf(price));
    }

    public static void throwCauseInvalidOrderStatus(OrderStatus status) {
        throw new PaymentException(status.getMessage());
    }
}
