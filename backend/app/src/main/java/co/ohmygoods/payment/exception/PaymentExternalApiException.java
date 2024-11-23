package co.ohmygoods.payment.exception;

import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

public class PaymentExternalApiException extends RuntimeException {

    public PaymentExternalApiException() {
    }

    public PaymentExternalApiException(String message) {
        super(message);
    }

    public static void throwAtPreparePhase(ConvertibleClientHttpResponse response) {
        throw new PaymentExternalApiException();
    }

    public static void throwAtApprovePhase(ConvertibleClientHttpResponse response) {
        throw new PaymentExternalApiException();
    }
}
