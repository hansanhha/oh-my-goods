package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentEndResponse(boolean isPaymentSuccess,
                                 String accountEmail,
                                 Long paymentId,
                                 String orderTransactionId,
                                 int paymentAmount,
                                 ExternalPaymentVendor externalPaymentVendor,
                                 PaymentStatus paymentFailureCause,
                                 LocalDateTime startedAt,
                                 LocalDateTime endedAt) {

    public static PaymentEndResponse success(String accountEmail, Long paymentId, String orderTransactionId,
                                             int paymentAmount, ExternalPaymentVendor externalPaymentVendor,
                                             LocalDateTime startedAt, LocalDateTime endedAt) {
        return new PaymentEndResponse(true, accountEmail, paymentId, orderTransactionId,
                paymentAmount, externalPaymentVendor, null, startedAt, endedAt);
    }

    public static PaymentEndResponse fail(String accountEmail, Long paymentId, String orderTransactionId,
                                          int paymentAmount, ExternalPaymentVendor externalPaymentVendor,
                                          PaymentStatus paymentFailureCause, LocalDateTime startedAt,
                                          LocalDateTime endedAt) {
        return new PaymentEndResponse(false, accountEmail, paymentId , orderTransactionId,
                paymentAmount, externalPaymentVendor, paymentFailureCause, startedAt, endedAt);
    }
}
