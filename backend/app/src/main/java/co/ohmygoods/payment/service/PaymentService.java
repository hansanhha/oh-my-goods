package co.ohmygoods.payment.service;

import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.vo.PaymentStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

public interface PaymentService {

    PaymentReadyResponse ready(UserAgent userAgent, String accountEmail, Long orderId, String paymentName);

    PaymentApproveResponse approve(String orderNumber, Map<String, String> properties);

    void fail(String transactionId);

    void cancel(String transactionId);

    boolean canPay(ExternalPaymentVendor externalPaymentVendor);

    enum UserAgent {
        DESKTOP,
        MOBILE_WEB,
        MOBILE_APP
    }

    record PaymentReadyResponse(String transactionId,
                                Long orderId,
                                String buyerEmail,
                                int paymentAmount,
                                boolean isReady,
                                LocalDateTime readyAt,
                                String nextUrl,
                                PaymentStatus paymentFailureCause,
                                ExternalPaymentError externalPaymentError) {

        static PaymentReadyResponse success(String transactionId, Long orderId, String buyerEmail, int paymentAmount, String nextUrl, LocalDateTime readyAt) {
            return new PaymentReadyResponse(transactionId, orderId, buyerEmail, paymentAmount,true, readyAt, nextUrl, null, null);
        }

        static PaymentReadyResponse fail(int paymentAmount, PaymentStatus paymentFailureCause, String externalServiceErrorCode, String externalServiceErrorMsg) {
            return new PaymentReadyResponse(null, null, null, paymentAmount, false, null, null, paymentFailureCause, new ExternalPaymentError(externalServiceErrorCode, externalServiceErrorMsg));
        }
    }

    record PaymentApproveResponse(boolean isApproved,
                                  Long paymentId,
                                  Long orderId,
                                  String accountEmail,
                                  int paymentAmount,
                                  String vendorName,
                                  PaymentStatus paymentStatus,
                                  ExternalPaymentError externalPaymentError,
                                  LocalDateTime approvedAt) {

        public static PaymentApproveResponse fail(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                                  String vendorName, PaymentStatus paymentStatus, ExternalPaymentError externalPaymentError) {

            return new PaymentApproveResponse(false, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, externalPaymentError, null);
        }

        public static PaymentApproveResponse success(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                                     String vendorName, PaymentStatus paymentStatus, LocalDateTime approvedAt) {
            return new PaymentApproveResponse(true, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, null, approvedAt);
        }

    }

    record ExternalPaymentError(String errorCode,
                                String errorMsg) {

    }
}
