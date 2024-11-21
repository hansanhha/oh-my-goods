package co.ohmygoods.order.exception;

import co.ohmygoods.order.vo.OrderStatus;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }

    public static void throwCauseInvalidPurchaseQuantity(int quantity) {
        throw new OrderException(String.valueOf(quantity));
    }

    public static void throwCauseInvalidOrderStatus(OrderStatus orderStatus) {
        throw new OrderException(orderStatus.getMessage());
    }

    public static void throwCauseCannotUpdateStatus(OrderStatus orderStatus) {
        throw new OrderException(orderStatus.getMessage());
    }
}
