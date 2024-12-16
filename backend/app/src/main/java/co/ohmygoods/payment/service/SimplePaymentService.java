package co.ohmygoods.payment.service;

import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.entity.vo.UserAgent;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.product.service.dto.PaymentApproveResponse;
import co.ohmygoods.product.service.dto.PaymentReadyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class SimplePaymentService implements PaymentService {

    @Override
    public Payment createPayment(ExternalPaymentVendor externalPaymentVendor, String accountEmail, Long orderId, int paymentAmount, String paymentName) {
        return null;
    }

    @Override
    public Payment getPayment(String orderTransactionId) {
        return null;
    }
}
