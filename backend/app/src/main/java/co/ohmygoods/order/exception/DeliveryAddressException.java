package co.ohmygoods.order.exception;

import org.springframework.util.StringUtils;

public class DeliveryAddressException extends RuntimeException {

    public DeliveryAddressException() {
    }

    public DeliveryAddressException(String message) {
        super(message);
    }

    public static void throwCauseInvalidInput(String... inputs) {
        throw new DeliveryAddressException(StringUtils.arrayToDelimitedString(inputs, ","));
    }
}
