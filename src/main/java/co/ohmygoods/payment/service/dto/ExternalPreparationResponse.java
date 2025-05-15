package co.ohmygoods.payment.service.dto;

import java.time.LocalDateTime;

public record ExternalPreparationResponse(boolean isSuccess,
                                          String accountEmail,
                                          String orderTransactionId,
                                          String externalTransactionId,
                                          String nextRedirectURI,
                                          int paymentAmount,
                                          ExternalPaymentError externalError,
                                          LocalDateTime createdAt,
                                          LocalDateTime preparedAt) {

    public static ExternalPreparationResponse fail(String accountEmail, String orderTransactionId,
                                                   int paymentAmount, ExternalPaymentError externalError,
                                                   LocalDateTime createdAt) {
        return new ExternalPreparationResponse(false, accountEmail, orderTransactionId,
                null, null, paymentAmount, externalError, createdAt, null);
    }

    public static ExternalPreparationResponse success(String accountEmail, String orderTransactionId,
                                                      String externalTransactionId, String nextRedirectURI,
                                                      int paymentAmount, LocalDateTime createdAt, LocalDateTime preparedAt) {
        return new ExternalPreparationResponse(true, accountEmail, orderTransactionId,
                externalTransactionId, nextRedirectURI, paymentAmount, null, createdAt, preparedAt);
    }
}
