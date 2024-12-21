package co.ohmygoods.payment.service;

import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.service.dto.*;
import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/*
    결제 처리 흐름
    - 결제 시작: 외부 결제 준비 api 요청, 결제 엔티티 생성
    - 결제 진행: 외부 결제 승인 api 요청, 응답값에 따른 결제 엔티티 상태 변경, 콜백 리스너 호출

    PaymentGateway 역할
    1. 결제 시작과 결제 진행 시 적절한 외부 결제 api 서비스 호출
    2. 외부 결제 api 응답에 따른 결제 처리 서비스 호출 및 응답 DTO 반환

    todo
        결제 취소, 실패 메서드 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentService paymentService;
    private final List<PaymentExternalRequestService> paymentExternalRequestServices;
    private final List<PaymentProcessListener> paymentProcessListeners;

    public PaymentStartResponse startPayment(PreparePaymentRequest request) {
        Long paymentId = paymentService.createPayment(request.externalPaymentVendor(),
                request.accountEmail(), request.orderId(), request.paymentAmount(), request.paymentName());

        LocalDateTime startedAt = LocalDateTime.now();

        PaymentExternalRequestService externalApiService = findSupportPaymentExternalApiService(request.externalPaymentVendor());

        ExternalPreparationResponse externalResponse = externalApiService.sendPreparationRequest(request.userAgent(),
                request.accountEmail(), request.orderTransactionId(), request.paymentAmount(), request.paymentName());

        if (!externalResponse.isSuccess()) {
            LocalDateTime failedAt = LocalDateTime.now();

            paymentService.failPayment(request.orderTransactionId(), externalResponse.externalError().paymentFailureCause(), failedAt);

            paymentProcessListeners.forEach(listener ->
                    listener.onFailure(paymentId, externalResponse.externalError().paymentFailureCause()));

            return PaymentStartResponse.fail(request.accountEmail(), request.paymentAmount(), request.externalPaymentVendor(),
                    externalResponse.externalError().paymentFailureCause(), startedAt, failedAt);
        }

        paymentService.readyPayment(paymentId, externalResponse.externalTransactionId(), externalResponse.preparedAt());

        return PaymentStartResponse.success(request.accountEmail(), paymentId, request.paymentAmount(),
                request.externalPaymentVendor(),externalResponse.nextRedirectURI(), externalResponse.createdAt(), externalResponse.preparedAt());
    }

    public PaymentEndResponse continuePayment(ApprovePaymentRequest request) {
        LocalDateTime continuedAt = LocalDateTime.now();

        PaymentExternalRequestService externalApiService = findSupportPaymentExternalApiService(request.externalPaymentVendor());

        ExternalApprovalResponse externalResponse = externalApiService.sendApprovalRequest(request.orderTransactionId(), request.properties());

        if (!externalResponse.isSuccess()) {
            LocalDateTime failedAt = LocalDateTime.now();

            Long paymentId = paymentService.failPayment(request.orderTransactionId(), externalResponse.externalError().paymentFailureCause(), failedAt);

            paymentProcessListeners.forEach(listener ->
                    listener.onFailure(paymentId, externalResponse.externalError().paymentFailureCause()));

            return PaymentEndResponse.fail(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                    externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.externalError().paymentFailureCause(),
                    continuedAt, failedAt);
        }

        Long paymentId = paymentService.successPayment(externalResponse.externalTransactionId(), externalResponse.approvedAt());

        paymentProcessListeners.forEach(listener -> listener.onSuccess(paymentId));

        return PaymentEndResponse.success(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.startedAt(), externalResponse.approvedAt());
    }

    public void cancelPayment(ExternalPaymentVendor vendor, String orderTransactionId) {
        Long paymentId = paymentService.cancelPayment(orderTransactionId, LocalDateTime.now());

        paymentProcessListeners.forEach(listener -> listener.onCancel(paymentId));
    }

    public void failPayment(ExternalPaymentVendor vendor, String orderTransactionId, Object failureInfo) {
        PaymentExternalRequestService externalApiService = findSupportPaymentExternalApiService(vendor);

        ExternalPaymentError externalPaymentError = externalApiService.extractExternalPaymentError(failureInfo);

        Long paymentId = paymentService.failPayment(orderTransactionId, externalPaymentError.paymentFailureCause(), LocalDateTime.now());

        paymentProcessListeners.forEach(listener ->
                listener.onFailure(paymentId, externalPaymentError.paymentFailureCause()));
    }

    private PaymentExternalRequestService findSupportPaymentExternalApiService(ExternalPaymentVendor externalPaymentVendor) {
        return paymentExternalRequestServices
                .stream()
                .filter(service -> service.isSupport(externalPaymentVendor))
                .findFirst()
                .orElseThrow(() -> PaymentException.notSupportPaymentVendor(externalPaymentVendor.name()));
    }
}
