package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.repository.ShopRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import static co.ohmygoods.payment.service.KakaopayService.*;

@Transactional
@Service
public class KakaopayService
        extends AbstractExternalPaymentApiService<KakaopayPreparationResponse, KakaopayApprovalResponse, KakaopayRequestFailureCause>
        implements PaymentService {

    private static final ExternalPaymentVendor KAKAOPAY = ExternalPaymentVendor.KAKAOPAY;

    private final PaymentServiceConfig.KakaoPayProperties kakaoPayProperties;
    private final RestClient kakaoPayApiClient;

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public KakaopayService(PaymentServiceConfig.KakaoPayProperties kakaoPayProperties, @Qualifier("kakaopayApiRestClient") RestClient kakaoPayApiClient,
                           ShopRepository shopRepository, OrderRepository orderRepository, AccountRepository accountRepository, PaymentRepository paymentRepository) {
        this.kakaoPayProperties = kakaoPayProperties;
        this.kakaoPayApiClient = kakaoPayApiClient;
        this.shopRepository = shopRepository;
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
                ? URI.create(kakaoPayProperties.getPreparationRequestUrl())
                : URI.create(kakaoPayProperties.getApprovalRequestUrl());
    }

    @Override
    public PaymentReadyResponse ready(UserAgent userAgent, String accountEmail, Long orderId, String paymentName) {
        OAuth2Account buyer = accountRepository.findByEmail(accountEmail).orElseThrow(() -> PaymentException.notFoundAccount(accountEmail));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> PaymentException.notFoundOrder(orderId));

        Payment payment = Payment.start(buyer, order, KAKAOPAY, order.getTotalPrice());
        paymentRepository.save(payment);

        KakaopayPreparationRequest kakaoPayPreparationRequest =
                KakaopayPreparationRequest.create(payment, buyer, order.getTransactionId(), paymentName, kakaoPayProperties);

        PreparationResult<KakaopayPreparationResponse, KakaopayRequestFailureCause> externalPreparationResult =
                sendExternalPaymentPreparationRequest(kakaoPayPreparationRequest);

        if (!externalPreparationResult.success()) {
            KakaopayRequestFailureCause externalError = externalPreparationResult.externalError();
            handlePaymentFailure(payment, externalError);

            return externalError != null
                    ? PaymentReadyResponse.fail(externalError.errorCode(), externalError.errorMessage())
                    : PaymentReadyResponse.fail(null, "unknown error");
        }

        KakaopayPreparationResponse preparationResponse = externalPreparationResult.preparationResponse();
        payment.ready(preparationResponse.tid(), LocalDateTime.ofInstant(preparationResponse.createdAt().toInstant(), ZoneId.systemDefault()));

        return PaymentReadyResponse.success(payment.getTransactionId(), order.getId(), accountEmail,
                getNextRedirectUrlByUserAgent(userAgent, preparationResponse), LocalDateTime.ofInstant(preparationResponse.createdAt().toInstant(), ZoneId.systemDefault()));
    }

    @Override
    public PaymentApproveResponse approve(String orderTransactionId, Map<String, String> properties) {
        Order order = orderRepository.fetchAccountByTransactionId(orderTransactionId).orElseThrow(() -> PaymentException.notFoundOrder(orderTransactionId));
        Payment payment = paymentRepository.findByOrder(order).orElseThrow(() -> PaymentException.notFoundPayment(order.getId()));
        OAuth2Account account = order.getAccount();

        KakaopayApprovalRequest kakaoPayApprovalRequest = new KakaopayApprovalRequest(kakaoPayProperties.getCid(),
                payment.getTransactionId(), order.getTransactionId(), account.getEmail(), properties.get("pgToken"));

        ApprovalResult<KakaopayApprovalResponse, KakaopayRequestFailureCause> externalApprovalResult =
                sendExternalPaymentApprovalRequest(kakaoPayApprovalRequest);

        if (!externalApprovalResult.success()) {
            KakaopayRequestFailureCause externalError = externalApprovalResult.externalError();
            handlePaymentFailure(payment, externalError);

            return PaymentApproveResponse.fail(payment.getId(),
                    order.getId(), account.getEmail(), payment.getPaymentAmount(),
                    payment.getExternalPaymentVendor().name(),  payment.getStatus(),
                    externalError != null ? new ExternalPaymentError(externalError.errorCode(), externalError.errorMessage()) : null);
        }

        payment.succeed();
        return PaymentApproveResponse.success(payment.getId(), order.getId(), account.getEmail(),
                payment.getPaymentAmount(), payment.getExternalPaymentVendor().name(),
                payment.getStatus(), LocalDateTime.from(externalApprovalResult.approvalResponse().approvedAt().toInstant()));
    }

    @Override
    public void fail(String transactionId) {
        Payment payment = paymentRepository.findFetchOrderAndProductByTransactionId(transactionId).orElseThrow(() -> PaymentException.notFoundPayment(transactionId));
        payment.fail(PaymentStatus.PAYMENT_FAILED_TIMEOUT); // 임시
    }

    @Override
    public void cancel(String transactionId) {
        Payment payment = paymentRepository.findFetchOrderAndProductByTransactionId(transactionId).orElseThrow(() -> PaymentException.notFoundPayment(transactionId));
        payment.cancel();
    }

    @Override
    public boolean canPay(ExternalPaymentVendor externalPaymentVendor) {
        return externalPaymentVendor.equals(KAKAOPAY);
    }

    @Override
    protected TypeReference<KakaopayPreparationResponse> getPreprationResponseMappingTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<KakaopayApprovalResponse> getApprovalResponseMappingTypeReference() {
        return new TypeReference<>() {};
    }

    @Override
    protected TypeReference<KakaopayRequestFailureCause> getExternalErrorMappingTypeReference() {
        return new TypeReference<>() {};
    }

    private void handlePaymentFailure(Payment payment, KakaopayRequestFailureCause kakaoPayRequestFailureCause) {
        if (kakaoPayRequestFailureCause == null) {
            payment.fail(PaymentStatus.PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR);
            return;
        }

        payment.fail(convertPaymentFailedStatus(kakaoPayRequestFailureCause));
    }

    private PaymentStatus convertPaymentFailedStatus(KakaopayRequestFailureCause kakaoPayApiFailResponse) {
        return switch (kakaoPayApiFailResponse.errorCode()) {
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

        private static KakaopayPreparationRequest create(Payment payment, OAuth2Account account, String orderTransactionId,
                                                         String paymentName, PaymentServiceConfig.KakaoPayProperties kakaoPayProperties) {
            return new KakaopayPreparationRequest(
                    kakaoPayProperties.getCid(),
                    orderTransactionId,
                    account.getEmail(),
                    paymentName,
                    1,
                    payment.getPaymentAmount(),
                    payment.getPaymentAmount(),
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
