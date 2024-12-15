package co.ohmygoods.payment.dto;

import co.ohmygoods.payment.vo.ExternalPaymentVendor;

import java.util.Map;

public record ApprovePaymentRequest(ExternalPaymentVendor externalPaymentVendor,
                                    String orderTransactionId,
                                    Map<String, String> properties) {
}
