package co.ohmygoods.payment.dto;

import co.ohmygoods.payment.service.PaymentService;

import java.time.LocalDateTime;

public record PreparePaymentResponse(boolean isPrepareSuccess,
                                     PaymentService.UserAgent userAgent,
                                     Long shopId,
                                     String buyerEmail,
                                     Long orderId,
                                     int totalPrice,
                                     String vendorName,
                                     String redirectUrl,
                                     String failureCauseMessage,
                                     LocalDateTime attemptedAt,
                                     LocalDateTime preparedAt) {

    public static PreparePaymentResponse success(PreparePaymentRequest request, PaymentService.ReadyResponse response, LocalDateTime attemptedAt) {
        return new PreparePaymentResponse(true, request.userAgent(), request.shopId(),
                request.buyerEmail(), request.orderId(), request.totalPrice(), request.externalPaymentVendor().name(),
                response.nextUrl(), null, attemptedAt, response.readyAt());
    }

    public static PreparePaymentResponse fail(PreparePaymentRequest request, PaymentService.ReadyResponse response, LocalDateTime attemptedAt) {
        return new PreparePaymentResponse(true, request.userAgent(), request.shopId(),
                request.buyerEmail(), request.orderId(), request.totalPrice(), request.externalPaymentVendor().name(),
                null, response.externalPaymentError().errorMsg(), attemptedAt, null);
    }
}
