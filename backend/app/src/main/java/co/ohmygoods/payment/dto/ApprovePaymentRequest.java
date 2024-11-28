package co.ohmygoods.payment.dto;

import co.ohmygoods.payment.vo.ExternalPaymentVendor;

import java.util.Map;

public record ApprovePaymentRequest(ExternalPaymentVendor externalPaymentVendor,
                                    String orderNumber,
                                    Map<String, String> properties) {
}
