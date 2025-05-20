package co.ohmygoods.product.service.dto;

import co.ohmygoods.payment.service.dto.PaymentAPIErrorDetail;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentReadyResponse(
        String transactionId,
        Long orderId,
        String buyerEmail,
        int paymentAmount,
        boolean isReady,
        LocalDateTime readyAt,
        String nextUrl,
        PaymentStatus paymentFailureCause,
        PaymentAPIErrorDetail externalPaymentError) {

    static PaymentReadyResponse success(String transactionId, Long orderId, String buyerEmail, int paymentAmount, String nextUrl, LocalDateTime readyAt) {
        return new PaymentReadyResponse(transactionId, orderId, buyerEmail, paymentAmount, true, readyAt, nextUrl, null, null);
    }

    static PaymentReadyResponse fail(int paymentAmount, PaymentStatus paymentFailureCause, String externalServiceErrorCode, String externalServiceErrorMsg) {
//        return new PaymentReadyResponse(null, null, null, paymentAmount, false, null, null, paymentFailureCause, new ExternalPaymentError(externalServiceErrorCode, externalServiceErrorMsg));
        return null;
    }
}
