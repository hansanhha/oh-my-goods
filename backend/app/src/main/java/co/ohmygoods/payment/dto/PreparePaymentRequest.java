package co.ohmygoods.payment.dto;

import co.ohmygoods.payment.service.PaymentService;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;

public record PreparePaymentRequest(ExternalPaymentVendor externalPaymentVendor,
                                    PaymentService.UserAgent userAgent,
                                    Long shopId,
                                    String buyerEmail,
                                    Long orderId,
                                    int totalPrice) {
}
