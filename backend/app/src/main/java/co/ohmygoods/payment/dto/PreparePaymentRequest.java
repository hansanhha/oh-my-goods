package co.ohmygoods.payment.dto;

import co.ohmygoods.payment.service.PaymentService;

public record PreparePaymentRequest(String vendorName,
                                    PaymentService.UserAgent userAgent,
                                    Long shopId,
                                    String buyerEmail,
                                    Long orderId,
                                    int totalPrice) {
}
