package co.ohmygoods.payment.service.dto;

import java.time.LocalDateTime;

public record PaymentPrepareAPIResponse(
        boolean isSuccessful,
        String email,
        String orderTransactionId,
        String externalTransactionId,
        String nextRedirectURI,
        int paymentAmount,
        PaymentAPIErrorDetail externalError,
        LocalDateTime requestAt,
        LocalDateTime preparedAt) {
                
        public static PaymentPrepareAPIResponse fail(String email, String orderTransactionId, int paymentAmount, PaymentAPIErrorDetail externalError,
                                                     LocalDateTime createdAt) {
                return new PaymentPrepareAPIResponse(false, email, orderTransactionId,
                                null, null, paymentAmount, externalError, createdAt, null);
        }

        public static PaymentPrepareAPIResponse success(String email, String orderTransactionId,
                                                        String externalTransactionId, String nextRedirectURI,
                                                        int paymentAmount, LocalDateTime createdAt, LocalDateTime preparedAt) {
                return new PaymentPrepareAPIResponse(true, email, orderTransactionId,
                                externalTransactionId, nextRedirectURI, paymentAmount, null, createdAt, preparedAt);
        }
}
