package co.ohmygoods.order.exception;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class AddressException extends RuntimeException {

    public AddressException(String message) {
        super(message);
    }

    public static void throwCauseInvalidInput(String... inputs) {
        throw new AddressException(StringUtils.arrayToDelimitedString(inputs, ","));
    }
}
