package co.ohmygoods.payment.service;

import co.ohmygoods.order.vo.OrderStatus;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.PaymentVendor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {

    ReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice);

    ApproveResponse approve(String transactionId, Map<String, String> properties);

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

    @Builder
    record ApproveResponse(boolean isApproved,
                           Long paymentId,
                           Long orderId,
                           String buyerEmail,
                           Long productId,
                           String productName,
                           int orderedQuantity,
                           int totalPrice,
                           String vendorName,
                           OrderStatus orderStatus,
                           PaymentStatus paymentStatus,
                           LocalDateTime approvedAt,
                           ExternalPaymentError externalPaymentError) {
    }

    record ExternalPaymentError(String errorCode,
                                String errorMsg) {

    }
}
