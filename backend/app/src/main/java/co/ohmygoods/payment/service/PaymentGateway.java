package co.ohmygoods.payment.service;

import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.service.dto.*;
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

    todo
        결제 취소, 실패 메서드 구현

    fixme
        현재 PaymentService 구현체에서 외부 api 요청과 내부 결제 처리를 모두 담당하고 있음
        외부 결제 API 요청 처리 서비스와 애플리케이션 결제 처리 서비스 객체 분리 필요
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentService paymentService;
    private final List<PaymentExternalApiService> paymentExternalApiServices;
    private final List<PaymentResultListener> paymentResultListeners;

    public PaymentStartResponse startPayment(PreparePaymentRequest request) {
        paymentService.createPayment(request.externalPaymentVendor(),
                request.accountEmail(), request.orderId(), request.paymentAmount(), request.paymentName());

        LocalDateTime startedAt = LocalDateTime.now();

        PaymentExternalApiService externalApiService = findSupportPaymentExternalApiService(request.externalPaymentVendor());

        ExternalPreparationResponse externalResponse = externalApiService.sendPreparationRequest(request.userAgent(),
                request.accountEmail(), request.orderTransactionId(), request.paymentAmount(), request.paymentName());

        if (!externalResponse.isSuccess()) {
            LocalDateTime failedAt = LocalDateTime.now();

            paymentService.failPayment(request.orderTransactionId(), externalResponse.externalError().paymentFailureCause(), failedAt);

            return PaymentStartResponse.fail(request.accountEmail(), request.paymentAmount(), request.externalPaymentVendor(),
                    externalResponse.externalError().paymentFailureCause(), startedAt, failedAt);
        }

        Long paymentId = paymentService.getPaymentId(request.orderTransactionId());
        paymentService.readyPayment(externalResponse.externalTransactionId(), externalResponse.preparedAt());

        return PaymentStartResponse.success(request.accountEmail(), paymentId, request.paymentAmount(),
                request.externalPaymentVendor(),externalResponse.nextRedirectURI(), externalResponse.createdAt(), externalResponse.preparedAt());
    }

    public PaymentEndResponse continuePayment(ApprovePaymentRequest request) {
        LocalDateTime continuedAt = LocalDateTime.now();

        PaymentExternalApiService externalApiService = findSupportPaymentExternalApiService(request.externalPaymentVendor());

        ExternalApprovalResponse externalResponse = externalApiService.sendApprovalRequest(request.orderTransactionId(), request.properties());

        Long paymentId = paymentService.getPaymentId(request.orderTransactionId());

        if (!externalResponse.isSuccess()) {
            LocalDateTime failedAt = LocalDateTime.now();

            paymentService.failPayment(request.orderTransactionId(), externalResponse.externalError().paymentFailureCause(), failedAt);

            return PaymentEndResponse.fail(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                    externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.externalError().paymentFailureCause(),
                    continuedAt, failedAt);
        }

        paymentService.readyPayment(externalResponse.externalTransactionId(), externalResponse.approvedAt());

        return PaymentEndResponse.success(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.startedAt(), externalResponse.approvedAt());
    }

    public void cancelPayment(ExternalPaymentVendor externalPaymentVendor, String orderTransactionId) {
        paymentService.cancelPayment(orderTransactionId, LocalDateTime.now());
    }

    public void failPayment(ExternalPaymentVendor externalPaymentVendor, String orderTransactionId) {
//        paymentService.failPayment(orderTransactionId, LocalDateTime.now());
    }

    private PaymentExternalApiService findSupportPaymentExternalApiService(ExternalPaymentVendor externalPaymentVendor) {
        return paymentExternalApiServices
                .stream()
                .filter(service -> service.isSupport(externalPaymentVendor))
                .findFirst()
                .orElseThrow(() -> PaymentException.notSupportPaymentVendor(externalPaymentVendor.name()));
    }
}
