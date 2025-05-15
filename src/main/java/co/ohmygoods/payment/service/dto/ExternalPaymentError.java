package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentStatus;

public record ExternalPaymentError(PaymentStatus paymentFailureCause,
                                   String externalErrorCode,
                                   String externalErrorMsg) {
}
