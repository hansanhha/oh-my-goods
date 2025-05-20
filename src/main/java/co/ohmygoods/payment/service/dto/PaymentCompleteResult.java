package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentCompleteResult(boolean isPaymentSuccess,
                                    String accountEmail,
                                    Long paymentId,
                                    String orderTransactionId,
                                    int paymentAmount,
                                    PaymentAPIProvider paymentAPIProvider,
                                    PaymentStatus paymentFailureCause,
                                    LocalDateTime requestAt,
                                    LocalDateTime completeAt) {

    public static PaymentCompleteResult success(String accountEmail, Long paymentId, String orderTransactionId,
                                                int paymentAmount, PaymentAPIProvider paymentAPIProvider,
                                                LocalDateTime startedAt, LocalDateTime endedAt) {
        return new PaymentCompleteResult(true, accountEmail, paymentId, orderTransactionId,
                paymentAmount, paymentAPIProvider, null, startedAt, endedAt);
    }

    public static PaymentCompleteResult fail(String accountEmail, Long paymentId, String orderTransactionId,
                                             int paymentAmount, PaymentAPIProvider paymentAPIProvider,
                                             PaymentStatus paymentFailureCause, LocalDateTime startedAt,
                                             LocalDateTime endedAt) {
        return new PaymentCompleteResult(false, accountEmail, paymentId , orderTransactionId,
                paymentAmount, paymentAPIProvider, paymentFailureCause, startedAt, endedAt);
    }
}
