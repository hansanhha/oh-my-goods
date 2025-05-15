package co.ohmygoods.product.service.dto;

import co.ohmygoods.payment.service.dto.ExternalPaymentError;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentApproveResponse(
        boolean isApproved,
        Long paymentId,
        Long orderId,
        String accountEmail,
        int paymentAmount,
        String vendorName,
        PaymentStatus paymentStatus,
        ExternalPaymentError externalPaymentError,
        LocalDateTime approvedAt) {

    public static PaymentApproveResponse fail(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                              String vendorName, PaymentStatus paymentStatus, ExternalPaymentError externalPaymentError) {

        return new PaymentApproveResponse(false, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, externalPaymentError, null);
    }

    public static PaymentApproveResponse success(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                                 String vendorName, PaymentStatus paymentStatus, LocalDateTime approvedAt) {
        return new PaymentApproveResponse(true, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, null, approvedAt);
    }

}
