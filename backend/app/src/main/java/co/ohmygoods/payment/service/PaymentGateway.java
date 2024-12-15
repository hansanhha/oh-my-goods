package co.ohmygoods.payment.service;

import co.ohmygoods.payment.dto.*;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentGateway {

    private final List<PaymentService> paymentServices;
    private final List<PaymentResultListener> paymentResultListeners;

    public PreparePaymentResponse preparePayment(PreparePaymentRequest request) {
        LocalDateTime attemptAt = LocalDateTime.now();

        PaymentService paymentService = findSupportPaymentService(request.externalPaymentVendor());
        PaymentService.PaymentReadyResponse response = paymentService.ready(request.userAgent(),
                request.accountEmail(), request.orderId(), request.paymentName());

        return response.isReady()
                ? PreparePaymentResponse.success(request, response, attemptAt)
                : PreparePaymentResponse.fail(request, response, attemptAt);
    }

    public ApprovePaymentResponse approvePayment(ApprovePaymentRequest request) {
        LocalDateTime attemptAt = LocalDateTime.now();

        PaymentService paymentService = findSupportPaymentService(request.externalPaymentVendor());
        PaymentService.PaymentApproveResponse response = paymentService.approve(request.orderTransactionId(), request.properties());

        if (!response.isApproved()) {
            paymentResultListeners.forEach(listener ->
                    listener.onFailure(response.paymentId(), response.orderId(), response.paymentStatus()));
            return ApprovePaymentResponse.fail(request, response, attemptAt);
        }

        paymentResultListeners.forEach(listener ->
                listener.onSuccess(response.paymentId(), response.orderId()));
        return ApprovePaymentResponse.success(request, response, attemptAt);
    }

    public void cancelPayment(ExternalPaymentVendor externalPaymentVendor, String transactionId) {
        PaymentService paymentService = findSupportPaymentService(externalPaymentVendor);
        paymentService.cancel(transactionId);

    }

    public void failPayment(ExternalPaymentVendor externalPaymentVendor, String transactionId) {
        PaymentService paymentService = findSupportPaymentService(externalPaymentVendor);
        paymentService.fail(transactionId);
    }

    private PaymentService findSupportPaymentService(ExternalPaymentVendor externalPaymentVendor) {
        return paymentServices
                .stream()
                .filter(service -> service.canPay(externalPaymentVendor))
                .findFirst()
                .orElseThrow(() -> PaymentException.notSupportPaymentVendor(externalPaymentVendor.name()));
    }
}
