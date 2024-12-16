package co.ohmygoods.payment.service;

import co.ohmygoods.payment.entity.vo.UserAgent;
import co.ohmygoods.payment.service.dto.ExternalApprovalResponse;
import co.ohmygoods.payment.service.dto.ExternalPreparationResponse;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;

import java.util.Map;

public interface PaymentExternalApiService {

    ExternalPreparationResponse sendPreparationRequest(UserAgent userAgent, String accountEmail, String orderTransactionId, int paymentAmount, String paymentName);

    ExternalApprovalResponse sendApprovalRequest(String orderTransactionId, Map<String, String> properties);

    boolean isSupport(ExternalPaymentVendor externalPaymentVendor);
}
