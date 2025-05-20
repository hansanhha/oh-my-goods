package co.ohmygoods.payment.service;


import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.model.event.PaymentCancelEvent;
import co.ohmygoods.payment.model.event.PaymentFailureEvent;
import co.ohmygoods.payment.model.event.PaymentSuccessEvent;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import co.ohmygoods.payment.service.dto.*;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
    <ul>결제 처리 과정</ul>
    <ol>결제 시작(start): 외부 결제 준비 api 요청, 결제 엔티티 생성</ol>
    <ol>결제 완료(complete): 외부 결제 승인 api 요청, 응답값에 따른 결제 엔티티 상태 변경, 콜백 리스너 호출</ol>

    <ul>PaymentGateway 역할</ul>
    <ol>외부 결제 api 호출</ol>
    <ol>api 응답에 따른 결제 후처리(이벤트 발행)</ol>
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentGateway {

    private final PaymentService paymentService;
    private final List<PaymentAPIService> paymentExternalRequestServices;
    private final ApplicationEventPublisher paymentEventPublisher;

    public PaymentStartResult start(PaymentPrepareAPIRequest request) {
        Long paymentId = paymentService.createPayment(request.paymentAPIProvider(),
                request.email(), request.orderId(), request.paymentAmount(), request.paymentName());

        PaymentAPIService paymentAPIService = findSupportPaymentExternalApiService(request.paymentAPIProvider());

        PaymentPrepareAPIResponse prepareResponse = paymentAPIService.prepare(request.userAgent(),
                request.email(), request.orderTransactionId(), request.paymentAmount(), request.paymentName());

        if (!prepareResponse.isSuccessful()) {
            LocalDateTime failedAt = LocalDateTime.now();

            paymentService.failPayment(request.orderTransactionId(), prepareResponse.externalError().paymentFailureCause(), failedAt);
            paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, prepareResponse.externalError().paymentFailureCause()));

            return PaymentStartResult.fail(request.email(), request.paymentAmount(), request.paymentAPIProvider(),
                    prepareResponse.externalError().paymentFailureCause(), prepareResponse.requestAt(), failedAt);
        }

        paymentService.readyPayment(paymentId, prepareResponse.externalTransactionId(), prepareResponse.preparedAt());

        return PaymentStartResult.success(request.email(), paymentId, request.paymentAmount(),
                request.paymentAPIProvider(), prepareResponse.nextRedirectURI(), prepareResponse.requestAt(), prepareResponse.preparedAt());
    }

    public PaymentCompleteResult complete(PaymentApproveAPIRequest request) {
        PaymentAPIService paymentAPIService = findSupportPaymentExternalApiService(request.paymentAPIProvider());

        PaymentApproveAPIResponse approveResponse = paymentAPIService.approve(request.orderTransactionId(), request.properties());

        if (!approveResponse.isSuccessful()) {
            LocalDateTime failedAt = LocalDateTime.now();

            Long paymentId = paymentService.failPayment(request.orderTransactionId(), approveResponse.externalError().paymentFailureCause(), failedAt);

            logPaymentResult(request, approveResponse, paymentId, failedAt, false);

            paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, approveResponse.externalError().paymentFailureCause()));

            return PaymentCompleteResult.fail(approveResponse.email(), paymentId, request.orderTransactionId(),
                    approveResponse.paymentAmount(), request.paymentAPIProvider(), approveResponse.externalError().paymentFailureCause(),
                    approveResponse.requestAt(), failedAt);
        }

        Long paymentId = paymentService.successPayment(approveResponse.externalTransactionId(), approveResponse.approvedAt());

        logPaymentResult(request, approveResponse, paymentId, approveResponse.approvedAt(), true);

        paymentEventPublisher.publishEvent(new PaymentSuccessEvent(paymentId));

        return PaymentCompleteResult.success(approveResponse.email(), paymentId, request.orderTransactionId(),
                approveResponse.paymentAmount(), request.paymentAPIProvider(), approveResponse.requestAt(), approveResponse.approvedAt());
    }

    public void cancelPayment(PaymentAPIProvider vendor, String orderTransactionId) {
        Long paymentId = paymentService.cancelPayment(orderTransactionId, LocalDateTime.now());

        paymentEventPublisher.publishEvent(new PaymentCancelEvent(paymentId));
    }

    public void failPayment(PaymentAPIProvider vendor, String orderTransactionId, String errorCode, String errorMessage) {
        PaymentAPIService paymentAPIService = findSupportPaymentExternalApiService(vendor);

        PaymentAPIErrorDetail paymentAPIErrorDetail = paymentAPIService.convertErrorDetail(errorCode, errorMessage);

        Long paymentId = paymentService.failPayment(orderTransactionId, paymentAPIErrorDetail.paymentFailureCause(), LocalDateTime.now());

        paymentEventPublisher.publishEvent(new PaymentFailureEvent(paymentId, paymentAPIErrorDetail.paymentFailureCause()));
    }

    private void logPaymentResult(PaymentApproveAPIRequest request,
                                  PaymentApproveAPIResponse externalResponse,
                                  Long paymentId, LocalDateTime completedAt, boolean success) {

        String defaultMessage = MessageFormat
                .format("account email: {0} orderTransactionId: {1} paymentId: {2} provider: {3} requestAt: {4} completeAt: {5}",
                        externalResponse.email(), externalResponse.orderTransactionId(), paymentId,
                        request.paymentAPIProvider().name().toLowerCase(), externalResponse.requestAt(), completedAt);

        StringBuilder logMessageBuilder = new StringBuilder();

        if (success) {
            logMessageBuilder.append("payment successful. ");
            logMessageBuilder.append(defaultMessage);
            log.info(logMessageBuilder.toString());
            return;
        }

        PaymentAPIErrorDetail externalPaymentError = externalResponse.externalError();
        PaymentStatus failureCause = externalPaymentError.paymentFailureCause();

        String failureCauseMessage = MessageFormat
                .format(" cause: {0} [provider error code: {1} error message: {2}]",
                        failureCause.getMessage(), externalPaymentError.errorCode(),
                        externalPaymentError.errorMessage());

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

    private PaymentAPIService findSupportPaymentExternalApiService(PaymentAPIProvider paymentAPIProvider) {
        return paymentExternalRequestServices
                .stream()
                .filter(service -> service.isSupport(paymentAPIProvider))
                .findFirst()
                .orElseThrow(PaymentException::unsupportedPaymentMethod);
    }
}
