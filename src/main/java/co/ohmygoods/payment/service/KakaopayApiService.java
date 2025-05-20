package co.ohmygoods.payment.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.model.entity.Payment;
import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.service.dto.PaymentAPIErrorDetail;
import co.ohmygoods.payment.service.dto.PaymentApproveAPIResponse;
import co.ohmygoods.payment.service.dto.PaymentPrepareAPIResponse;
import co.ohmygoods.payment.model.vo.PaymentAPIProvider;
import co.ohmygoods.payment.model.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import static co.ohmygoods.payment.service.PaymentAPIUtils.*;
import static co.ohmygoods.payment.service.PaymentAPIUtils.PaymentPhase.*;


@Transactional
@Service
@RequiredArgsConstructor
public class KakaopayAPIService implements PaymentAPIService {

    private final PaymentServiceConfig.KakaoPayProperties kakaopayProperties;
    private final RestClient kakaoPayAPIClient;

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentPrepareAPIResponse prepare(UserAgent userAgent, String email, String orderTransactionId,
                                             int paymentAmount, String paymentName) {

        Account account = accountRepository.findByEmail(email).orElseThrow(AuthException::notFoundAccount);
        Order order = orderRepository.fetchAccountByTransactionId(orderTransactionId).orElseThrow(OrderException::notFoundOrder);

        KakaopayPrepareRequest kakaopayPrepareRequest = KakaopayPrepareRequest.create(paymentAmount, account, order.getTransactionId(), paymentName, kakaopayProperties);

        KakaopayAPIResponse apiResponse = sendKakaopayAPIRequest(PREPARE, kakaopayPrepareRequest);

        if (!apiResponse.isSuccessful()) {
            return PaymentPrepareAPIResponse.fail(email, orderTransactionId, paymentAmount, convertToPaymentAPIErrorDetail(apiResponse.errorDetail()), apiResponse.requestAt());
        }

        KakaopayPrepareResponse prepareResponse = apiResponse.prepareResponse();
        return PaymentPrepareAPIResponse.success(email, orderTransactionId, prepareResponse.tid(), selectNextRedirectUrl(userAgent, prepareResponse),
                paymentAmount, apiResponse.requestAt(), toLocalDateTime(prepareResponse.createdAt()));
    }

    @Override
    public PaymentApproveAPIResponse approve(String orderTransactionId, Map<String, String> properties) {
        Order order = orderRepository.fetchAccountByTransactionId(orderTransactionId).orElseThrow(OrderException::notFoundOrder);
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(PaymentException::notFoundPayment);
        Account account = order.getAccount();

        KakaopayApproveRequest kakaoApproveRequest = new KakaopayApproveRequest(kakaopayProperties.getCid(), payment.getTransactionId(), order.getTransactionId(), account.getEmail(), properties.get("pgToken"));

        KakaopayAPIResponse apiResponse = sendKakaopayAPIRequest(APPROVE, kakaoApproveRequest);

        if (!apiResponse.isSuccessful()) {
            return PaymentApproveAPIResponse.fail(account.getEmail(), orderTransactionId, payment.getTransactionId(),
                    payment.getPaymentAmount(), convertToPaymentAPIErrorDetail(apiResponse.errorDetail()), apiResponse.requestAt());
        }

        KakaopayApproveResponse approveResponse = apiResponse.approveResponse();
        return PaymentApproveAPIResponse.success(account.getEmail(), orderTransactionId, payment.getTransactionId(),
                payment.getPaymentAmount(), apiResponse.requestAt(), toLocalDateTime(approveResponse.approvedAt()));
    }

    private KakaopayAPIResponse sendKakaopayAPIRequest(PaymentAPIUtils.PaymentPhase phase, Object requestBody) {
        LocalDateTime requestAt = LocalDateTime.now();

        return kakaoPayAPIClient
                .post()
                .uri(phase.equals(PREPARE) ? kakaopayProperties.getPrepareUrl() : kakaopayProperties.getApproveUrl())
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, kakaopayProperties.getSecretKey());
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .body(PaymentAPIUtils.convertToJson(requestBody))
                .exchange((request, response) -> {
                    HttpStatusCode status = response.getStatusCode();

                    if (status.isError()) {
                        return KakaopayAPIResponse.error(convertToDTO(response.getBody(), KakaopayErrorDetail.class), requestAt);
                    }

                    return phase.equals(PREPARE)
                            ? KakaopayAPIResponse.prepareSuccess(convertToDTO(response.getBody(), KakaopayPrepareResponse.class), requestAt)
                            : KakaopayAPIResponse.approveSuccess(convertToDTO(response.getBody(), KakaopayApproveResponse.class), requestAt);
                });
    }

    @Override
    public PaymentAPIErrorDetail convertErrorDetail(String errorCode, String errorMessage) {
        PaymentStatus failureStatus = switch (errorCode) {
            default ->
                    PaymentStatus.PAYMENT_FAILED_NETWORK_ERROR;
        };

        return new PaymentAPIErrorDetail(failureStatus, errorCode, errorMessage);
    }

    @Override
    public boolean isSupport(PaymentAPIProvider paymentAPIProvider) {
        return paymentAPIProvider.equals(PaymentAPIProvider.KAKAOPAY);
    }

    private PaymentAPIErrorDetail convertToPaymentAPIErrorDetail(KakaopayErrorDetail errorDetail) {
        return convertErrorDetail(errorDetail.errorCode(), errorDetail.errorMessage());
    }


    private String selectNextRedirectUrl(UserAgent userAgent, KakaopayPrepareResponse preparationResponse) {
        return switch (userAgent) {
            case DESKTOP ->
                    preparationResponse.nextRedirectPcUrl();
            case MOBILE_WEB ->
                    preparationResponse.nextRedirectMobileUrl();
            case MOBILE_APP ->
                    preparationResponse.nextRedirectAppUrl();
        };
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    private record KakaopayPrepareRequest(
            String cid,
            String partnerOrderId,
            String partnerUserId,
            String itemName,
            int quantity,
            int totalAmount,
            int taxFreeAmount,
            String approvalURL,
            String cancelURL,
            String failURL) {

        private static KakaopayPrepareRequest create(int paymentAmount, Account account, String orderTransactionId,
                                                     String paymentName, PaymentServiceConfig.KakaoPayProperties kakaoPayProperties) {
            return new KakaopayPrepareRequest(
                    kakaoPayProperties.getCid(),
                    orderTransactionId,
                    account.getEmail(),
                    paymentName,
                    1,
                    paymentAmount,
                    paymentAmount,
                    kakaoPayProperties.getApproveRedirectUrl().concat("?order_number=").concat(orderTransactionId),
                    kakaoPayProperties.getCancelRedirectUrl(),
                    kakaoPayProperties.getFailRedirectUrl());
        }
    }

    private record KakaopayApproveRequest(
            String cid,
            String tid,
            String partnerOrderId,
            String partnerUserid,
            String pgToken) {

    }

    private record KakaopayAPIResponse(
            boolean isSuccessful,
            KakaopayPrepareResponse prepareResponse,
            KakaopayApproveResponse approveResponse,
            KakaopayErrorDetail errorDetail,
            LocalDateTime requestAt) {

        private static KakaopayAPIResponse error(KakaopayErrorDetail errorDetail, LocalDateTime requestAt) {
            return new KakaopayAPIResponse(false, null, null, errorDetail, requestAt);
        }

        private static KakaopayAPIResponse prepareSuccess(KakaopayPrepareResponse prepareResponse, LocalDateTime requestAt) {
            return new KakaopayAPIResponse(true, prepareResponse, null, null, requestAt);
        }

        private static KakaopayAPIResponse approveSuccess(KakaopayApproveResponse approveResponse, LocalDateTime requestAt) {
            return new KakaopayAPIResponse(true, null, approveResponse, null, requestAt);
        }

    }


    private record KakaopayPrepareResponse(
            String tid,
            String nextRedirectAppUrl,
            String nextRedirectMobileUrl,
            String nextRedirectPcUrl,
            String androidAppScheme,
            String iosAppScheme,
            Date createdAt) {

    }

    private record KakaopayApproveResponse(
            String aid,
            String tid,
            String cid,
            String partnerOrderId,
            String partnerUserId,
            String paymentMethodType,
            String ItemName,
            int quantity,
            Amount amount,
            CardInfo cardInfo,
            Date createdAt,
            Date approvedAt,
            String payload) {


        record Amount(
                int total,
                int taxFree,
                int vat,
                int point,
                int discount,
                int greenDeposit) {


        }

        record CardInfo(
                String kakaopayPurchaseCorp,
                String kakaopayPurchaseCorpCode,
                String kakaopayIssuerCorp,
                String kakaopayIssuerCorpCode,
                String bin,
                String cardType,
                String installMonth,
                String approvedId,
                String cardMid,
                String interestFreeInstall,
                String installmentType,
                String cardItemCode) {

        }

    }

    private record KakaopayErrorDetail(
            String errorCode,
            String errorMessage,
            Extras extras) {

        record Extras(
                String methodResultCode,
                String methodResultMessage) {

        }
    }
}
