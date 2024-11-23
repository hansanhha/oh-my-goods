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
public class KakaoPayService extends AbstractExternalPaymentApiService implements PaymentService {

    private final PaymentProperties.KakaoPayProperties kakaoPayProperties;
    private final RestClient kakaoPayApiClient;

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    public KakaoPayService(PaymentProperties.KakaoPayProperties kakaoPayProperties,
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

        KakaoPayPreparationRequest kakaoPayPreparationRequest = KakaoPayPreparationRequest.create(payment, buyer, order, order.getProduct(), kakaoPayProperties);
        PreparationResult<KakaoPayPreparationResponse> result = sendExternalPreparationRequest(kakaoPayPreparationRequest);

        if (!result.isSuccess()) {
            Optional<KakaoPayApiFailCause> kakaoPayApiFailureCause = extractExternalFailureCause(result.getPreparationResponseBody(), KakaoPayApiFailCause.class);

            if (kakaoPayApiFailureCause.isEmpty()) {
                payment.fail(PaymentStatus.PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR);
                return PaymentReadyResponse.failure(null);
            }

            KakaoPayApiFailCause kakaoPayApiFailCause = kakaoPayApiFailureCause.get();
            payment.fail(convertPaymentFailedStatus(kakaoPayApiFailCause));

            return PaymentReadyResponse.failure(kakaoPayApiFailCause.errorMessage());
        }

        KakaoPayPreparationResponse preparationResponse = result.getPreparationResponse();
        payment.ready(preparationResponse.tid(), preparationResponse.createdAt());

        return PaymentReadyResponse.success(getNextRedirectUrlByUserAgent(userAgent, preparationResponse));
    }

    @Override
    public void approve(String transactionId, Map<String, String> properties) {

    }

    @Override
    public void fail(String transactionId) {

    }

    @Override
    public void cancel(String transactionId) {

    }

    private PaymentStatus convertPaymentFailedStatus(KakaoPayApiFailCause kakaoPayApiFailResponse) {
        return switch (kakaoPayApiFailResponse.errorCode()) {
            default -> PaymentStatus.PAYMENT_FAILED_NETWORK_ERROR;
        };
    }

    private String getNextRedirectUrlByUserAgent(UserAgent userAgent, KakaoPayPreparationResponse preparationResponse) {
        return switch (userAgent) {
            case DESKTOP -> preparationResponse.nextRedirectPcUrl();
            case MOBILE_WEB -> preparationResponse.nextRedirectMobileUrl();
            case MOBILE_APP -> preparationResponse.nextRedirectAppUrl();
        };
    }


    record KakaoPayPreparationRequest(String cid,
                                      String partnerOrderId,
                                      String partnerUserId,
                                      String itemName,
                                      int quantity,
                                      int totalAmount,
                                      int taxFreeAmount,
                                      String approvalURL,
                                      String cancelURL,
                                      String failURL) {

        private static KakaoPayPreparationRequest create(Payment payment, OAuth2Account account, Order order,
                                                         Product product, PaymentProperties.KakaoPayProperties kakaoPayProperties) {
            return new KakaoPayPreparationRequest(
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

    record KakaoPayApprovalRequest() {

    }


    record KakaoPayPreparationResponse(String tid,
                                       String nextRedirectAppUrl,
                                       String nextRedirectMobileUrl,
                                       String nextRedirectPcUrl,
                                       String androidAppScheme,
                                       String iosAppScheme,
                                       LocalDateTime createdAt) {

    }

    record KakaoPayApprovalResponse() {

    }

    private record KakaoPayApiFailCause(String errorCode,
                                        String errorMessage) {

    }
}
