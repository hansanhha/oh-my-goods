package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.entity.Payment;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.vo.PaymentProperties;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.PaymentVendor;
import co.ohmygoods.product.entity.Product;
import co.ohmygoods.shop.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import com.google.common.net.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
public class KakaopayService extends AbstractExternalPaymentApiService implements PaymentService {

    private final PaymentProperties.KakaoPayProperties kakaoPayProperties;
    private final RestClient kakaoPayApiClient;

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public KakaopayService(PaymentProperties.KakaoPayProperties kakaoPayProperties,
                           ShopRepository shopRepository,
                           OrderRepository orderRepository,
                           AccountRepository accountRepository,
                           PaymentRepository paymentRepository) {

        this.kakaoPayProperties = kakaoPayProperties;
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;

        kakaoPayApiClient = RestClient.builder()
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, kakaoPayProperties.getSecretKey());
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
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
    public PaymentReadyResponse ready(UserAgent userAgent, Long shopId, String buyerEmail, Long orderId, int totalPrice) {
        Shop shop = shopRepository.findById(shopId).orElseThrow(() -> PaymentException.notFoundShop(shopId));
        OAuth2Account buyer = accountRepository.findByEmail(buyerEmail).orElseThrow(() -> PaymentException.notFoundAccount(buyerEmail));
        Order order = orderRepository.findById(orderId).orElseThrow(() -> PaymentException.notFoundOrder(orderId));

        Payment payment = Payment.create(shop, buyer, order, PaymentVendor.KAKAOPAY, totalPrice);
        paymentRepository.save(payment);

        KakaopayPreparationRequest kakaoPayPreparationRequest = KakaopayPreparationRequest.create(payment, buyer, order, order.getProduct(), kakaoPayProperties);
        PreparationResult<KakaopayPreparationResponse> result = sendExternalPreparationRequest(kakaoPayPreparationRequest);

        if (!result.isSuccess()) {
            Optional<KakaopayRequestFailureCause> kakaoPayRequestFailureCause = extractExternalFailureCause(result.getPreparationResponseBody(), KakaopayRequestFailureCause.class);
            handlePaymentFailure(payment, kakaoPayRequestFailureCause);

            return kakaoPayRequestFailureCause
                    .map(cause -> PaymentReadyResponse.failure(cause.errorMessage()))
                    .orElseGet(() -> PaymentReadyResponse.failure(null));
        }

        KakaopayPreparationResponse preparationResponse = result.getPreparationResponse();
        payment.ready(preparationResponse.tid(), preparationResponse.createdAt());

        return PaymentReadyResponse.success(getNextRedirectUrlByUserAgent(userAgent, preparationResponse));
    }

    @Override
    public void approve(String transactionId, Map<String, String> properties) {
        Payment payment = paymentRepository.findFetchOrderAndAccountByTransactionId(transactionId).orElseThrow(() -> PaymentException.notFoundPayment(transactionId));

        Order order = payment.getOrder();
        OAuth2Account account = order.getAccount();

        KakaopayApprovalRequest kakaoPayApprovalRequest = new KakaopayApprovalRequest(kakaoPayProperties.getCid(),
                transactionId, order.getOrderNumber(),
                account.getEmail(),
                properties.get("pgToken"));
        ApprovalResult<KakaopayApprovalResponse> approvalResult = sendExternalApprovalRequest(kakaoPayApprovalRequest);

        if (!approvalResult.isSuccess()) {
            Optional<KakaopayRequestFailureCause> kakaoPayRequestFailureCause = extractExternalFailureCause(approvalResult.getApprovalResponseBody(), KakaopayRequestFailureCause.class);
            handlePaymentFailure(payment, kakaoPayRequestFailureCause);
            return;
        }

        payment.succeed();
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

    private void handlePaymentFailure(Payment payment, Optional<KakaopayRequestFailureCause> kakaoPayRequestFailureCause) {
        if (kakaoPayRequestFailureCause.isEmpty()) {
            payment.fail(PaymentStatus.PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR);
            return;
        }

        payment.fail(convertPaymentFailedStatus(kakaoPayRequestFailureCause.get()));
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


    record KakaopayPreparationRequest(String cid,
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
                                                         Product product, PaymentProperties.KakaoPayProperties kakaoPayProperties) {
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

    record KakaopayApprovalRequest(String cid,
                                   String tid,
                                   String partnerOrderId,
                                   String partnerUserid,
                                   String pgToken) {

    }


    record KakaopayPreparationResponse(String tid,
                                       String nextRedirectAppUrl,
                                       String nextRedirectMobileUrl,
                                       String nextRedirectPcUrl,
                                       String androidAppScheme,
                                       String iosAppScheme,
                                       LocalDateTime createdAt) {

    }

    record KakaopayApprovalResponse(String aid,
                                    String tid,
                                    String cid,
                                    String partnerOrderId,
                                    String partnerUserId,
                                    String paymentMethodType,
                                    String ItemName,
                                    int quantity,
                                    Amount amount,
                                    CardInfo cardInfo,
                                    LocalDateTime createdAt,
                                    LocalDateTime approvedAt,
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

    private record KakaopayRequestFailureCause(String errorCode,
                                               String errorMessage) {

    }
}
