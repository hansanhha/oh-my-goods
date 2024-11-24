package co.ohmygoods.payment.service;

import co.ohmygoods.payment.vo.PaymentVendor;

import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {

    ReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice);

    void approve(String transactionId, Map<String, String> properties);

    void fail(String transactionId);

    void cancel(String transactionId);

    boolean canPay(PaymentVendor paymentVendor);

    enum UserAgent {
        DESKTOP,
        MOBILE_WEB,
        MOBILE_APP
    }

    record ReadyResponse(boolean isReady,
                         LocalDateTime readyAt,
                         String nextUrl,
                         ExternalPaymentError externalPaymentError) {

        static ReadyResponse ready(String nextUrl, LocalDateTime readyAt) {
            return new ReadyResponse(true, readyAt, nextUrl, null);
        }

        static ReadyResponse readyFailed(String externalServiceErrorCode, String externalServiceErrorMsg) {
            return new ReadyResponse(false, null, null, new ExternalPaymentError(externalServiceErrorCode, externalServiceErrorMsg));
        }
    }

    record ApproveResponse(boolean isApproved,
                           LocalDateTime approvedAt,
                           ExternalPaymentError externalPaymentError) {

        static ApproveResponse approve(LocalDateTime approvedAt) {
            return new ApproveResponse(true, approvedAt, null);
        }

        static ApproveResponse approveFailed(String externalServiceErrorCode, String externalServiceErrorMsg) {
            return new ApproveResponse(false, null, new ExternalPaymentError(externalServiceErrorCode, externalServiceErrorMsg));
        }
    }

    record ExternalPaymentError(String errorCode,
                                String errorMsg) {

    }
}
