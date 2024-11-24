package co.ohmygoods.payment.dto;

import java.util.Map;

public record ApprovePaymentRequest(String vendorName,
                                    String transactionId,
                                    Map<String, String> properties) {
}
