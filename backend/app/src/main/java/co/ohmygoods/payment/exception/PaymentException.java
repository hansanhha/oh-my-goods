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

    public static PaymentException notFoundShop(Long shopId) {
        return new PaymentException(String.valueOf(shopId));
    }

    public static PaymentException notFoundAccount(String buyerEmail) {
        return new PaymentException(buyerEmail);
    }

    public static PaymentException notFoundOrder(Long orderId) {
        return new PaymentException(String.valueOf(orderId));
    }
}
