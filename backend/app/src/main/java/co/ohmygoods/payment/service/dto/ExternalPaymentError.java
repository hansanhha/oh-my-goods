package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.vo.PaymentStatus;

public record ExternalPaymentError(PaymentStatus paymentFailureCause,
                                   String externalErrorCode,
                                   String externalErrorMsg) {
}
