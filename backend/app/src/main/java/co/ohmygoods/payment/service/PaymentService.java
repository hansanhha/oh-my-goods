package co.ohmygoods.payment.service;

import co.ohmygoods.order.vo.OrderStatus;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {

    ReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice);

    ApproveResponse approve(String transactionId, Map<String, String> properties);

    void fail(String transactionId);

    void cancel(String transactionId);

    boolean canPay(ExternalPaymentVendor externalPaymentVendor);

    enum UserAgent {
        DESKTOP,
        MOBILE_WEB,
        MOBILE_APP
    }

    record ReadyResponse(String transactionId,
                         Long orderId,
                         String buyerEmail,
                         boolean isReady,
                         LocalDateTime readyAt,
                         String nextUrl,
                         ExternalPaymentError externalPaymentError) {

        static ReadyResponse success(String transactionId, Long orderId, String buyerEmail, String nextUrl, LocalDateTime readyAt) {
            return new ReadyResponse(transactionId, orderId, buyerEmail, true, readyAt, nextUrl, null);
        }

        static ReadyResponse fail(String externalServiceErrorCode, String externalServiceErrorMsg) {
            return new ReadyResponse(null, null, null, false, null, null, new ExternalPaymentError(externalServiceErrorCode, externalServiceErrorMsg));
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
