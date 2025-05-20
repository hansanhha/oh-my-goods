package co.ohmygoods.payment.service;


import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.service.dto.PaymentAPIErrorDetail;
import co.ohmygoods.payment.service.dto.PaymentApproveAPIResponse;
import co.ohmygoods.payment.service.dto.PaymentPrepareAPIResponse;


import java.util.Map;


public interface PaymentAPIService {

    PaymentPrepareAPIResponse prepare(UserAgent userAgent, String email, String orderTransactionId, int paymentAmount, String paymentName);

    PaymentApproveAPIResponse approve(String orderTransactionId, Map<String, String> properties);

    PaymentAPIErrorDetail convertErrorDetail(String errorCode, String errorMessage);

    boolean isSupport(PaymentAPIProvider paymentAPIProvider);

}
