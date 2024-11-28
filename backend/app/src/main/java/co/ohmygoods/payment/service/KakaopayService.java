package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.PaymentVendor;
import co.ohmygoods.product.entity.Product;
import co.ohmygoods.shop.entity.Shop;
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

    private static final PaymentVendor KAKAOPAY = PaymentVendor.KAKAOPAY;

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
    public ReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> PaymentException.notFoundShop(shopId));
        OAuth2Account buyer = accountRepository.findByEmail(buyerEmail).orElseThrow(() -> PaymentException.notFoundAccount(buyerEmail));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> PaymentException.notFoundOrder(orderId));

        Payment payment = Payment.create(shop, buyer, order, KAKAOPAY, totalPrice);
        paymentRepository.save(payment);

        KakaopayPreparationRequest kakaoPayPreparationRequest = KakaopayPreparationRequest.create(payment, buyer, order, order.getProduct(), kakaoPayProperties);
        PreparationResult<KakaopayPreparationResponse, KakaopayRequestFailureCause> externalPreparationResult = sendExternalPaymentPreparationRequest(kakaoPayPreparationRequest);

        if (!externalPreparationResult.success()) {
            KakaopayRequestFailureCause externalError = externalPreparationResult.externalError();
            handlePaymentFailure(payment, externalError);

            return externalError != null
                    ? ReadyResponse.readyFailed(externalError.errorCode(), externalError.errorMessage())
                    : ReadyResponse.readyFailed(null, "unknown error");
        }

        KakaopayPreparationResponse preparationResponse = externalPreparationResult.preparationResponse();
        payment.ready(preparationResponse.tid(), LocalDateTime.ofInstant(preparationResponse.createdAt().toInstant(), ZoneId.systemDefault()));

        return ReadyResponse.ready(getNextRedirectUrlByUserAgent(userAgent, preparationResponse), LocalDateTime.ofInstant(preparationResponse.createdAt().toInstant(), ZoneId.systemDefault()));
    }

    @Override
    public ApproveResponse approve(String transactionId, Map<String, String> properties) {
        Payment payment = paymentRepository.fetchByTransactionIdWithOrderAndAccountAndProduct(transactionId).orElseThrow(() -> PaymentException.notFoundPayment(transactionId));

        Order order = payment.getOrder();
        OAuth2Account account = order.getAccount();

        KakaopayApprovalRequest kakaoPayApprovalRequest = new KakaopayApprovalRequest(kakaoPayProperties.getCid(),
                transactionId, order.getOrderNumber(),
                account.getEmail(),
                properties.get("pgToken"));
        ApprovalResult<KakaopayApprovalResponse, KakaopayRequestFailureCause> externalApprovalResult = sendExternalPaymentApprovalRequest(kakaoPayApprovalRequest);

        if (!externalApprovalResult.success()) {
            KakaopayRequestFailureCause externalError = externalApprovalResult.externalError();
            handlePaymentFailure(payment, externalError);

            return createApproveFailResponse(payment, order, account, order.getProduct(), externalError);
        }

        payment.succeed();
        return createApproveSuccessResponse(payment, order, account, order.getProduct(), externalApprovalResult);
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
    public boolean canPay(PaymentVendor paymentVendor) {
        return paymentVendor.equals(KAKAOPAY);
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

    private ApproveResponse createApproveFailResponse(Payment payment, Order order, OAuth2Account account,
                                                      Product product, KakaopayRequestFailureCause cause) {
        return createApproveResponse(false, payment, order, account, product,
                null, cause != null ? new ExternalPaymentError(cause.errorCode(), cause.errorMessage()) : null);
    }

    private ApproveResponse createApproveSuccessResponse(Payment payment, Order order, OAuth2Account account,
                                                         Product product, ApprovalResult<KakaopayApprovalResponse, KakaopayRequestFailureCause> approvalResult) {
        return createApproveResponse(true, payment, order, account, product,
                LocalDateTime.from(approvalResult.approvalResponse().approvedAt().toInstant()), null);
    }

    private ApproveResponse createApproveResponse(boolean isApproved, Payment payment, Order order, OAuth2Account account, Product product,
                                                  LocalDateTime approvedAt, ExternalPaymentError externalPaymentError) {
        return ApproveResponse.builder()
                .isApproved(isApproved)
                .paymentId(payment.getId())
                .orderId(order.getId())
                .buyerEmail(account.getEmail())
                .productId(product.getId())
                .productName(product.getName())
                .orderedQuantity(order.getOrderedQuantity())
                .totalPrice(order.getDiscountedPrice())
                .vendorName(KAKAOPAY.name())
                .orderStatus(order.getStatus())
                .paymentStatus(payment.getStatus())
                .approvedAt(approvedAt)
                .externalPaymentError(externalPaymentError)
                .build();
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

        private static KakaopayPreparationRequest create(Payment payment, OAuth2Account account, Order order,
                                                         Product product, PaymentServiceConfig.KakaoPayProperties kakaoPayProperties) {
            return new KakaopayPreparationRequest(
                    kakaoPayProperties.getCid(),
                    order.getOrderNumber(),
                    account.getEmail(),
                    product.getName(),
                    order.getOrderedQuantity(),
                    order.getDiscountedPrice(),
                    order.getDiscountedPrice(),
                    kakaoPayProperties.getApprovalRedirectUrl(),
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
