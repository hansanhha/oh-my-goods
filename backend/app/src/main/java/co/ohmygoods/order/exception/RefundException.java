package co.ohmygoods.order.exception;

public class RefundException extends RuntimeException {

    public RefundException() {
    }

    public RefundException(String message) {
        super(message);
    }

  public static void throwCauseEmptyText() {
    throw new RefundException();
  }
}
