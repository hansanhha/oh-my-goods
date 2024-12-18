package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;

import java.util.Map;

public record ApprovePaymentRequest(ExternalPaymentVendor externalPaymentVendor,
                                    String orderTransactionId,
                                    Map<String, String> properties) {
}
