package co.ohmygoods.order.exception;

import co.ohmygoods.global.exception.DomainException;

public class OrderException extends DomainException {

    public static final OrderException NOT_FOUND_ORDER = new OrderException(OrderError.NOT_FOUND_ORDER);
    public static final OrderException NOT_FOUND_ORDER_ITEM = new OrderException(OrderError.NOT_FOUND_ORDER_ITEM);
    public static final OrderException NOT_FOUND_EXCHANGE = new OrderException(OrderError.NOT_FOUND_EXCHANGE);
    public static final OrderException NOT_FOUND_REFUND = new OrderException(OrderError.NOT_FOUND_REFUND);

    public static final OrderException INVALID_PURCHASE_AMOUNT = new OrderException(OrderError.INVALID_PURCHASE_AMOUNT);
    public static final OrderException INVALID_PURCHASE_QUANTITY = new OrderException(OrderError.INVALID_PURCHASE_QUANTITY);
    public static final OrderException INVALID_ORDER_STATUS = new OrderException(OrderError.INVALID_ORDER_STATUS);
    public static final OrderException CANNOT_UPDATE_ORDER_STATUS = new OrderException(OrderError.CANNOT_UPDATE_ORDER_STATUS);
    public static final OrderException CANNOT_UPDATE_ORDER_ITEM_STATUS = new OrderException(OrderError.CANNOT_UPDATE_ORDER_ITEM_STATUS);
    public static final OrderException CANNOT_UPDATE_DELIVERY_ADDRESS = new OrderException(OrderError.CANNOT_UPDATE_DELIVERY_ADDRESS);

    public static final OrderException ORDER_ALREADY_COMPLETED = new OrderException(OrderError.ORDER_ALREADY_COMPLETED);
    public static final OrderException ORDER_ALREADY_CANCELED = new OrderException(OrderError.ORDER_ALREADY_CANCELED);
    public static final OrderException ORDER_ALREADY_EXCHANGE_REQUESTED = new OrderException(OrderError.ORDER_ALREADY_EXCHANGE_REQUESTED);
    public static final OrderException ORDER_ALREADY_EXCHANGED = new OrderException(OrderError.ORDER_ALREADY_EXCHANGED);
    public static final OrderException ORDER_ALREADY_REFUND_REQUESTED = new OrderException(OrderError.ORDER_ALREADY_REFUND_REQUESTED);
    public static final OrderException ORDER_ALREADY_REFUNDED = new OrderException(OrderError.ORDER_ALREADY_REFUNDED);

    public static final OrderException INVALID_CS_REQUEST_REASON = new OrderException(OrderError.INVALID_CS_REQUEST_REASON);
    public static final OrderException INVALID_CS_RESPONSE = new OrderException(OrderError.INVALID_CS_REQUEST_REASON);

    public OrderException(OrderError orderError) {
        super(orderError);
    }

    public static OrderException notFoundOrder() {
        return NOT_FOUND_ORDER;
    }

    public static OrderException notFoundOrderItem() {
        return NOT_FOUND_ORDER_ITEM;
    }
}
