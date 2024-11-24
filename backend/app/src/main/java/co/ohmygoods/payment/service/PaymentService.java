package co.ohmygoods.payment.service;

import co.ohmygoods.payment.vo.PaymentVendor;

import java.util.Map;

public interface PaymentService {

    PaymentReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice);

    void approve(String transactionId, Map<String, String> properties);

    void fail(String transactionId);

    void cancel(String transactionId);

    boolean canPay(PaymentVendor paymentVendor);

    enum UserAgent {
        DESKTOP,
        MOBILE_WEB,
        MOBILE_APP
    }

    record PaymentReadyResponse(boolean success,
                                String nextUrl,
                                String failCauseMessage) {

        static PaymentReadyResponse success(String nextUrl) {
            return new PaymentReadyResponse(true, nextUrl, null);
        }

        static PaymentReadyResponse failure(String failCauseMessage) {
            return new PaymentReadyResponse(false, null, failCauseMessage);
        }
    }
}
