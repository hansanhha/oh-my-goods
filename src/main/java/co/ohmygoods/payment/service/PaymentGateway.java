package co.ohmygoods.payment.service;

import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.model.event.PaymentCancelEvent;
import co.ohmygoods.payment.model.event.PaymentFailureEvent;
import co.ohmygoods.payment.model.event.PaymentSuccessEvent;
import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import co.ohmygoods.payment.service.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
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
@Slf4j
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentService paymentService;
    private final List<PaymentExternalRequestService> paymentExternalRequestServices;
    private final ApplicationEventPublisher paymentEventPublisher;

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

            paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, externalResponse.externalError().paymentFailureCause()));

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

            logPaymentResult(request, externalResponse, paymentId, failedAt, false);

            paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, externalResponse.externalError().paymentFailureCause()));

            return PaymentEndResponse.fail(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                    externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.externalError().paymentFailureCause(),
                    continuedAt, failedAt);
        }

        Long paymentId = paymentService.successPayment(externalResponse.externalTransactionId(), externalResponse.approvedAt());

        logPaymentResult(request, externalResponse, paymentId, externalResponse.approvedAt(), true);

        paymentEventPublisher.publishEvent(new PaymentSuccessEvent(paymentId));

        return PaymentEndResponse.success(externalResponse.accountEmail(), paymentId, request.orderTransactionId(),
                externalResponse.paymentAmount(), request.externalPaymentVendor(), externalResponse.startedAt(), externalResponse.approvedAt());
    }

    public void cancelPayment(ExternalPaymentVendor vendor, String orderTransactionId) {
        Long paymentId = paymentService.cancelPayment(orderTransactionId, LocalDateTime.now());

        paymentEventPublisher.publishEvent(new PaymentCancelEvent(paymentId));
    }

    public void failPayment(ExternalPaymentVendor vendor, String orderTransactionId, Object failureInfo) {
        PaymentExternalRequestService externalApiService = findSupportPaymentExternalApiService(vendor);

        ExternalPaymentError externalPaymentError = externalApiService.extractExternalPaymentError(failureInfo);

        Long paymentId = paymentService.failPayment(orderTransactionId, externalPaymentError.paymentFailureCause(), LocalDateTime.now());

        paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, externalPaymentError.paymentFailureCause()));
    }

    private void logPaymentResult(ApprovePaymentRequest request,
                                  ExternalApprovalResponse externalResponse,
                                  Long paymentId, LocalDateTime completedAt, boolean success) {

        String defaultMessage = MessageFormat
                .format("account email: {0} orderTransactionId: {1} paymentId: {2} provider: {3} requestAt: {4} completeAt: {5}",
                        externalResponse.accountEmail(), externalResponse.orderTransactionId(), paymentId,
                        request.externalPaymentVendor().name().toLowerCase(), externalResponse.startedAt(), completedAt);

        StringBuilder logMessageBuilder = new StringBuilder();

        if (success) {
            logMessageBuilder.append("payment successful. ");
            logMessageBuilder.append(defaultMessage);
            log.info(logMessageBuilder.toString());
            return;
        }

        ExternalPaymentError externalPaymentError = externalResponse.externalError();
        PaymentStatus failureCause = externalPaymentError.paymentFailureCause();

        String failureCauseMessage = MessageFormat
                .format(" cause: {0} [provider error code: {1} error message: {2}]",
                        failureCause.getMessage(), externalPaymentError.externalErrorCode(),
                        externalPaymentError.externalErrorMsg());

        logMessageBuilder.append("payment failed. ");
        logMessageBuilder.append(defaultMessage);
        logMessageBuilder.append(failureCauseMessage);
        String paymentFailedMessage = logMessageBuilder.toString();

        if (failureCause.equals(PaymentStatus.PAYMENT_FAILED_NETWORK_ERROR)
                || failureCause.equals(PaymentStatus.PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR)) {
            log.warn(paymentFailedMessage);
            return;
        }

        log.info(paymentFailedMessage);
    }

    private PaymentExternalRequestService findSupportPaymentExternalApiService(ExternalPaymentVendor externalPaymentVendor) {
        return paymentExternalRequestServices
                .stream()
                .filter(service -> service.isSupport(externalPaymentVendor))
                .findFirst()
                .orElseThrow(PaymentException::unsupportedPaymentMethod);
    }
}
