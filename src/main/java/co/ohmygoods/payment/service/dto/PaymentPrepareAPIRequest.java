package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.UserAgent;

public record PaymentPrepareAPIRequest(
        PaymentAPIProvider paymentAPIProvider,
        UserAgent userAgent,
        String email,
        Long orderId,
        String orderTransactionId,
        int paymentAmount,
        String paymentName) {
}
