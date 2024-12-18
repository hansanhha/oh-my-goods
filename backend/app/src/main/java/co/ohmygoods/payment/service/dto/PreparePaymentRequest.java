package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;

public record PreparePaymentRequest(ExternalPaymentVendor externalPaymentVendor,
                                    UserAgent userAgent,
                                    String accountEmail,
                                    Long orderId,
                                    String orderTransactionId,
                                    int paymentAmount,
                                    String paymentName) {
}
