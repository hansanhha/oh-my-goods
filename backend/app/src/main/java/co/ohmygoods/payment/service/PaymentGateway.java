package co.ohmygoods.payment.service;

import co.ohmygoods.payment.dto.*;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/*
    결제 처리 흐름: 결제 준비 -> 결제 승인 -> 결제 완료

    PaymentGateway 역할
    1. 적절한 결제 서비스를 찾아 결제 처리를 위임
    2. 처리 결과에 따른 콜백 서비스 호출 및 DTO 반환

    fixme
        현재 PaymentService 구현체에서 외부 api 요청과 내부 결제 처리를 모두 담당하고 있음
        외부 결제 API 요청 처리 서비스와 애플리케이션 결제 처리 서비스 객체 분리 필요
 */
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
