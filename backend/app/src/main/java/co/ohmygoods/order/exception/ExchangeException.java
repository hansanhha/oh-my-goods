package co.ohmygoods.order.exception;

public class ExchangeException extends RuntimeException {

    public ExchangeException() {
    }

    public ExchangeException(String message) {
        super(message);
    }

    public static void throwCauseEmptyText() {
        throw new ExchangeException();
    }
}
