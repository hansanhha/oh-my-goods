package co.ohmygoods.payment.dto;

import java.util.Map;

public record ApprovePaymentRequest(String vendorName,
                                    String orderNumber,
                                    Map<String, String> properties) {
}
