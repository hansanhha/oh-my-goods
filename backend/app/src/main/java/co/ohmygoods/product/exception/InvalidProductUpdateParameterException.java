package co.ohmygoods.product.exception;

public class InvalidProductUpdateParameterException extends RuntimeException {

    public InvalidProductUpdateParameterException() {
    }

    public InvalidProductUpdateParameterException(String message) {
        super(message);
    }
}
