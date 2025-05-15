package co.ohmygoods.payment.exception;

import co.ohmygoods.global.exception.DomainException;

public class PaymentException extends DomainException {

    public static final PaymentException NOT_FOUND_PAYMENT = new PaymentException(PaymentError.NOT_FOUND_PAYMENT);

    public static final PaymentException INVALID_PAYMENT_METHOD = new PaymentException(PaymentError.INVALID_PAYMENT_METHOD);
    public static final PaymentException INVALID_PURCHASE_AMOUNT = new PaymentException(PaymentError.INVALID_PURCHASE_AMOUNT);
    public static final PaymentException CANNOT_UPDATE_PAYMENT_METHOD = new PaymentException(PaymentError.CANNOT_UPDATE_PAYMENT_METHOD);
    public static final PaymentException NOT_SUPPORTED_PAYMENT_METHOD = new PaymentException(PaymentError.NOT_SUPPORTED_PAYMENT_METHOD);

    public static final PaymentException FAILED_PAYMENT_API_REQUEST = new PaymentException(PaymentError.FAILED_PAYMENT_API_REQUEST);

    public PaymentException(PaymentError paymentError) {
        super(paymentError);
    }

    public static PaymentException notFoundPayment() {
        return NOT_FOUND_PAYMENT;
    }

    public static PaymentException unsupportedPaymentMethod() {
        return NOT_SUPPORTED_PAYMENT_METHOD;
    }
}
