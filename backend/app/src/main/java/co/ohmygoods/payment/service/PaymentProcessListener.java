package co.ohmygoods.payment.service;

import co.ohmygoods.payment.model.vo.PaymentStatus;

public interface PaymentProcessListener {

    void onSuccess(Long paymentId);

    void onCancel(Long paymentId);

    void onFailure(Long paymentId, PaymentStatus paymentFailureCause);
}
