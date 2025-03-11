package co.ohmygoods.payment.service.dto;

import java.time.LocalDateTime;

public record ExternalApprovalResponse(boolean isSuccess,
                                       String accountEmail,
                                       String orderTransactionId,
                                       String externalTransactionId,
                                       int paymentAmount,
                                       ExternalPaymentError externalError,
                                       LocalDateTime startedAt,
                                       LocalDateTime approvedAt) {

    public static ExternalApprovalResponse fail(String accountEmail, String orderTransactionId, String externalTransactionId,
                                                int paymentAmount, ExternalPaymentError externalError, LocalDateTime startedAt) {
        return new ExternalApprovalResponse(false, accountEmail, orderTransactionId,
                externalTransactionId, paymentAmount, externalError, startedAt, null);
    }

    public static ExternalApprovalResponse success(String accountEmail, String orderTransactionId, String externalTransactionId,
                                                      int paymentAmount, LocalDateTime startedAt, LocalDateTime approvedAt) {
        return new ExternalApprovalResponse(true, accountEmail, orderTransactionId,
                externalTransactionId, paymentAmount, null, startedAt, approvedAt);
    }
}
