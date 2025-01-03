package co.ohmygoods.order.exception;

import co.ohmygoods.global.exception.DomainException;

public class DeliveryAddressException extends DomainException {

    public static final DeliveryAddressException NOT_FOUND_DELIVERY_ADDRESS = new DeliveryAddressException(DeliveryAddressError.NOT_FOUND_DELIVERY_ADDRESS);
    public static final DeliveryAddressException INVALID_DELIVERY_ADDRESS = new DeliveryAddressException(DeliveryAddressError.INVALID_DELIVERY_ADDRESS);
    public static final DeliveryAddressException CANNOT_UPDATE_DELIVERY_ADDRESS = new DeliveryAddressException(DeliveryAddressError.CANNOT_UPDATE_DELIVERY_ADDRESS);


    public DeliveryAddressException(DeliveryAddressError deliveryAddressError) {
        super(deliveryAddressError);
    }

    public static DeliveryAddressException notFoundDeliveryAddress() {
        return NOT_FOUND_DELIVERY_ADDRESS;
    }
}
