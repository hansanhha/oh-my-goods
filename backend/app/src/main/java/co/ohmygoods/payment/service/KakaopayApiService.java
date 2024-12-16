package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.entity.vo.UserAgent;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.service.dto.ExternalPaymentError;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.payment.vo.PaymentStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static co.ohmygoods.payment.service.KakaopayApiService.*;

@Transactional
@Service
public class KakaopayApiService
        extends AbstractExternalPaymentApiService<KakaopayPreparationResponse, KakaopayApprovalResponse, KakaopayRequestFailureCause> {

    private final PaymentServiceConfig.KakaoPayProperties kakaopayProperties;
    private final RestClient kakaoPayApiClient;

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public KakaopayApiService(PaymentServiceConfig.KakaoPayProperties kakaopayProperties, @Qualifier("kakaopayApiRestClient") RestClient kakaoPayApiClient,
                              OrderRepository orderRepository, AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.kakaopayProperties = kakaopayProperties;
        this.kakaoPayApiClient = kakaoPayApiClient;
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    protected RestClient getExternalApiRestClient() {
        return kakaoPayApiClient;
    }

    @Override
    protected URI getExternalPaymentRequestUri(PaymentPhase paymentPhase) {
        return paymentPhase.equals(PaymentPhase.PREPARATION)
                ? URI.create(kakaopayProperties.getPreparationRequestUrl())
                : URI.create(kakaopayProperties.getApprovalRequestUrl());
    }

    @Override
    protected Object getPreparationRequestBody(String accountEmail, String orderTransactionId, int paymentAmount, String paymentName) {
        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(() -> PaymentException.notFoundAccount(accountEmail));
        Order order = orderRepository.fetchAccountByTransactionId(orderTransactionId).orElseThrow(() -> PaymentException.notFoundOrder(orderTransactionId));

        return KakaopayPreparationRequest.create(paymentAmount, account, order.getTransactionId(), paymentName, kakaopayProperties);
    }

    @Override
    protected Object getApprovalRequestBody(String orderTransactionId, Map<String, String> properties) {
        Order order = orderRepository.fetchAccountByTransactionId(orderTransactionId).orElseThrow(() -> PaymentException.notFoundOrder(orderTransactionId));
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> PaymentException.notFoundPayment(order.getId()));
        OAuth2Account account = order.getAccount();

        return new KakaopayApprovalRequest(kakaopayProperties.getCid(), payment.getTransactionId(),
                order.getTransactionId(), account.getEmail(), properties.get("pgToken"));
    }

    @Override
    protected PreparationResponseDetail extractPreparationResponseDetail(UserAgent userAgent, KakaopayPreparationResponse kpr) {
        return new PreparationResponseDetail(kpr.tid(), getNextRedirectUrlByUserAgent(userAgent, kpr),
                toLocalDateTime(kpr.createdAt()), toLocalDateTime(kpr.createdAt()));
    }

    @Override
    protected ApprovalResponseDetail extractApprovalResponseDetail(KakaopayApprovalResponse kpr) {
        return new ApprovalResponseDetail(kpr.partnerUserId(), kpr.tid(), kpr.amount().total(),
                toLocalDateTime(kpr.createdAt()), toLocalDateTime(kpr.approvedAt()));
    }

    @Override
    protected ExternalPaymentError convertToExternalError(KakaopayRequestFailureCause cause) {
        return convertKakaopayErrorToExternalError(cause);
    }

    @Override
    public boolean isSupport(ExternalPaymentVendor externalPaymentVendor) {
        return externalPaymentVendor.equals(ExternalPaymentVendor.KAKAOPAY);
    }

    private ExternalPaymentError convertKakaopayErrorToExternalError(KakaopayRequestFailureCause cause) {
        PaymentStatus paymentFailedStatus = convertPaymentFailedStatus(cause.errorCode());

        return new ExternalPaymentError(paymentFailedStatus, cause.errorCode(), cause.errorMessage());
    }

    private PaymentStatus convertPaymentFailedStatus(String kakaopayErrorCode) {
        return switch (kakaopayErrorCode) {
            default -> PaymentStatus.PAYMENT_FAILED_NETWORK_ERROR;
        };
    }

    private String getNextRedirectUrlByUserAgent(UserAgent userAgent, KakaopayPreparationResponse preparationResponse) {
        return switch (userAgent) {
            case DESKTOP -> preparationResponse.nextRedirectPcUrl();
            case MOBILE_WEB -> preparationResponse.nextRedirectMobileUrl();
            case MOBILE_APP -> preparationResponse.nextRedirectAppUrl();
        };
    }

    private LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
    }

    protected record KakaopayPreparationRequest(String cid,
                                      String partnerOrderId,
                                      String partnerUserId,
                                      String itemName,
                                      int quantity,
                                      int totalAmount,
                                      int taxFreeAmount,
                                      String approvalURL,
                                      String cancelURL,
                                      String failURL) {

        private static KakaopayPreparationRequest create(int paymentAmount, OAuth2Account account, String orderTransactionId,
                                                         String paymentName, PaymentServiceConfig.KakaoPayProperties kakaoPayProperties) {
            return new KakaopayPreparationRequest(
                    kakaoPayProperties.getCid(),
                    orderTransactionId,
                    account.getEmail(),
                    paymentName,
                    1,
                    paymentAmount,
                    paymentAmount,
                    kakaoPayProperties.getApprovalRedirectUrl().concat("?order_number=").concat(orderTransactionId),
                    kakaoPayProperties.getCancelRedirectUrl(),
                    kakaoPayProperties.getFailRedirectUrl());
        }
    }

    protected record KakaopayApprovalRequest(String cid,
                                   String tid,
                                   String partnerOrderId,
                                   String partnerUserid,
                                   String pgToken) {

    }


    protected record KakaopayPreparationResponse(String tid,
                                       String nextRedirectAppUrl,
                                       String nextRedirectMobileUrl,
                                       String nextRedirectPcUrl,
                                       String androidAppScheme,
                                       String iosAppScheme,
                                       Date createdAt) {

    }

    protected record KakaopayApprovalResponse(String aid,
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
                                    String payload
                                    ) {

        record Amount(int total,
                      int taxFree,
                      int vat,
                      int point,
                      int discount,
                      int greenDeposit) {

        }

        record CardInfo(String kakaopayPurchaseCorp,
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

    protected record KakaopayRequestFailureCause(String errorCode,
                                       String errorMessage,
                                       Extras extras) {

        record Extras(String methodResultCode, String methodResultMessage) {

        }
    }
}
