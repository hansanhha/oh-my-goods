package co.ohmygoods.payment.service.dto;

import java.time.LocalDateTime;

public record PaymentApproveAPIResponse(boolean isSuccessful,
                                        String email,
                                        String orderTransactionId,
                                        String externalTransactionId,
                                        int paymentAmount,
                                        PaymentAPIErrorDetail externalError,
                                        LocalDateTime requestAt,
                                        LocalDateTime approvedAt) {

    public static PaymentApproveAPIResponse fail(String email, String orderTransactionId, String externalTransactionId,
                                                 int paymentAmount, PaymentAPIErrorDetail externalError, LocalDateTime requestAt) {
        return new PaymentApproveAPIResponse(false, email, orderTransactionId,
                externalTransactionId, paymentAmount, externalError, requestAt, null);
    }

    public static PaymentApproveAPIResponse success(String email, String orderTransactionId, String externalTransactionId,
                                                    int paymentAmount, LocalDateTime requestAt, LocalDateTime approvedAt) {
        return new PaymentApproveAPIResponse(true, email, orderTransactionId,
                externalTransactionId, paymentAmount, null, requestAt, approvedAt);
    }
}
