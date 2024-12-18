package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.payment.service.dto.ExternalPreparationResponse;
import co.ohmygoods.shop.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = {KakaopayApiService.class, PaymentServiceConfig.class})
@EnableConfigurationProperties(PaymentServiceConfig.KakaoPayProperties.class)
@TestPropertySource(locations = "classpath:application.yml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KakaopayApiServiceTest {

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private KakaopayApiService kakaopayApiService;

    @Mock
    private OAuth2Account mockAccount;

    private static final String ACCOUNT_EMAIL = "test@email.com";
    private static final Long ORDER_ID = 1L;
    private static final String ORDER_TRANSACTION_ID = UUID.randomUUID().toString();
    private static final int ORDER_TOTAL_PRICE = 100_000;
    private static final int ORDER_DISCOUNT_PRICE = 20_000;
    private static final String PAYMENT_NAME = "ohmygoods 테스트 결제";

    private Order order;

    @BeforeEach
    void init() {
        order = Order.start(mockAccount,ORDER_TRANSACTION_ID,
                Collections.emptyList(), ORDER_TOTAL_PRICE,  ORDER_DISCOUNT_PRICE);
    }

    @Test
    void 결제_준비_성공() {
        when(mockAccount.getEmail()).thenReturn(ACCOUNT_EMAIL);
        when(accountRepository.findByEmail(anyString())).thenReturn(Optional.of(mockAccount));
        when(orderRepository.fetchAccountByTransactionId(anyString())).thenReturn(Optional.of(order));

        ExternalPreparationResponse kakaopayPreparationResponse = kakaopayApiService.sendPreparationRequest(UserAgent.DESKTOP,
                ACCOUNT_EMAIL, ORDER_TRANSACTION_ID, ORDER_TOTAL_PRICE, PAYMENT_NAME);

        System.out.println(kakaopayPreparationResponse);
    }

    @Test
    void 결제_승인_성공() {

    }

    @Test
    void Order가_준비되지_않으면_결제_준비_실패() {

    }

    @Test
    void 결제_준비_외부_API_요청_실패() {

    }

    @Test
    void 결제_승인_외부_API_요청_실패() {

    }

}