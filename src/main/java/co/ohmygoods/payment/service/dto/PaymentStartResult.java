package co.ohmygoods.payment.service.dto;

import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentStartResult(boolean isStartSuccess,
                                 String accountEmail,
                                 Long paymentId,
                                 int paymentAmount,
                                 PaymentAPIProvider paymentAPIProvider,
                                 String nextRedirectUrl,
                                 PaymentStatus paymentFailureCause,
                                 LocalDateTime startedAt,
                                 LocalDateTime completedAt) {

    public static PaymentStartResult success(String accountEmail, Long paymentId, int paymentAmount,
                                             PaymentAPIProvider paymentAPIProvider, String nextRedirectUrl,
                                             LocalDateTime startedAt, LocalDateTime completedAt) {
        return new PaymentStartResult(true, accountEmail, paymentId, paymentAmount,
                paymentAPIProvider, nextRedirectUrl, null, startedAt, completedAt);
    }

    public static PaymentStartResult fail(String accountEmail, int paymentAmount,
                                          PaymentAPIProvider paymentAPIProvider, PaymentStatus paymentFailureCause,
                                          LocalDateTime startedAt, LocalDateTime completedAt) {
        return new PaymentStartResult(false, accountEmail, null, paymentAmount,
                paymentAPIProvider, null, paymentFailureCause, startedAt, completedAt);
    }
}
