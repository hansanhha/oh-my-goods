package co.ohmygoods.order.exception;

public class CancelOrderException extends RuntimeException {
    public CancelOrderException() {
    }

    public CancelOrderException(String message) {
        super(message);
    }

    public static void throwCauseEmptyText() {
        throw new CancelOrderException();
    }
}
