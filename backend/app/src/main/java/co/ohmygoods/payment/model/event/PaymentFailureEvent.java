package co.ohmygoods.payment.model.event;

import co.ohmygoods.payment.model.vo.PaymentStatus;

public record PaymentFailureEvent(Long paymentId,
                                  PaymentStatus paymentFailureCause) {
}
