package co.ohmygoods.payment.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }

    public static void throwCauseInvalidPaymentPrice(int price) {
        throw new PaymentException(String.valueOf(price));
    }
}
