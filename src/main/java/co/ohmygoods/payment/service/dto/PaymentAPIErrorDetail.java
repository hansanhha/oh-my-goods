package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentStatus;

public record PaymentAPIErrorDetail(PaymentStatus paymentFailureCause,
                                   String errorCode,
                                   String errorMessage) {
}
