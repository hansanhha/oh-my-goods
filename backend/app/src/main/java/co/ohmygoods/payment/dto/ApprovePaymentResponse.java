package co.ohmygoods.payment.dto;

import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.payment.service.PaymentService;
import co.ohmygoods.payment.vo.PaymentStatus;

import java.time.LocalDateTime;

public record ApprovePaymentResponse(boolean isApproveSuccess,
                                     String transactionId,
                                     Long paymentId,
                                     Long orderId,
                                     String buyerEmail,
                                     Long productId,
                                     String productName,
                                     int orderedQuantity,
                                     int totalPrice,
                                     String processingVendorName,
                                     OrderStatus orderStatus,
                                     PaymentStatus paymentStatus,
                                     LocalDateTime attemptedAt,
                                     LocalDateTime approvedAt,
                                     String failCauseMsg) {

    public static ApprovePaymentResponse success(ApprovePaymentRequest request, PaymentService.ApproveResponse response, LocalDateTime attemptedAt) {
        return new ApprovePaymentResponse(true, request.orderNumber(), response.paymentId(),
                response.orderId(), response.buyerEmail(), response.productId(), response.productName(),
                response.orderedQuantity(), response.totalPrice(), response.vendorName(), response.orderStatus(),
                response.paymentStatus(), attemptedAt, response.approvedAt(), null);
    }

    public static ApprovePaymentResponse fail(ApprovePaymentRequest request, PaymentService.ApproveResponse response, LocalDateTime attemptedAt) {
        return new ApprovePaymentResponse(false, request.orderNumber(), response.paymentId(),
                response.orderId(), response.buyerEmail(), response.productId(), response.productName(),
                response.orderedQuantity(), response.totalPrice(), response.vendorName(), response.orderStatus(),
                response.paymentStatus(), attemptedAt, null, response.externalPaymentError().errorMsg());
    }
}
