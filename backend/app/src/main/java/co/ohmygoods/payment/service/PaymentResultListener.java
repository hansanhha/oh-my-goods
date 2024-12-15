package co.ohmygoods.payment.service;

import co.ohmygoods.payment.vo.PaymentStatus;

public interface PaymentResultListener {

    void onSuccess(Long paymentId, Long orderId);

    void onCancel(Long paymentId, Long orderId);

    void onFailure(Long paymentId, Long orderId, PaymentStatus failureCause);
}
