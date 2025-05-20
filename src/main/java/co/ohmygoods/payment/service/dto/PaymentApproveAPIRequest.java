package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentAPIProvider;

import java.util.Map;

public record PaymentApproveAPIRequest(
        PaymentAPIProvider paymentAPIProvider,
        String orderTransactionId,
        Map<String, String> properties) {
}
