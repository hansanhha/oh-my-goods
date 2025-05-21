package co.ohmygoods.product.service.user.dto;

import co.ohmygoods.payment.service.dto.PaymentAPIErrorDetail;
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
        PaymentAPIErrorDetail externalPaymentError,
        LocalDateTime approvedAt) {

    public static PaymentApproveResponse fail(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                              String vendorName, PaymentStatus paymentStatus, PaymentAPIErrorDetail externalPaymentError) {

        return new PaymentApproveResponse(false, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, externalPaymentError, null);
    }

    public static PaymentApproveResponse success(Long paymentId, Long orderId, String accountEmail, int paymentAmount,
                                                 String vendorName, PaymentStatus paymentStatus, LocalDateTime approvedAt) {
        return new PaymentApproveResponse(true, paymentId, orderId, accountEmail, paymentAmount, vendorName, paymentStatus, null, approvedAt);
    }

}
