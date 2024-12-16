package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentStartResponse(boolean isStartSuccess,
                                   String accountEmail,
                                   Long paymentId,
                                   int paymentAmount,
                                   ExternalPaymentVendor externalPaymentVendor,
                                   String nextRedirectUrl,
                                   PaymentStatus paymentFailureCause,
                                   LocalDateTime startedAt,
                                   LocalDateTime completedAt) {

    public static PaymentStartResponse success(String accountEmail, Long paymentId, int paymentAmount,
                                               ExternalPaymentVendor externalPaymentVendor, String nextRedirectUrl,
                                               LocalDateTime startedAt, LocalDateTime completedAt) {
        return new PaymentStartResponse(true, accountEmail, paymentId, paymentAmount,
                externalPaymentVendor, nextRedirectUrl, null, startedAt, completedAt);
    }

    public static PaymentStartResponse fail(String accountEmail, int paymentAmount,
                                            ExternalPaymentVendor externalPaymentVendor, PaymentStatus paymentFailureCause,
                                            LocalDateTime startedAt, LocalDateTime completedAt) {
        return new PaymentStartResponse(false, accountEmail, null, paymentAmount,
                externalPaymentVendor, null, paymentFailureCause, startedAt, completedAt);
    }
}
