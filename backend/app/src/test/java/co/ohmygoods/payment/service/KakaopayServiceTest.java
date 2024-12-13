package co.ohmygoods.payment.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.model.entity.Address;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.payment.config.PaymentServiceConfig;
import co.ohmygoods.payment.repository.PaymentRepository;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductTopCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.shop.entity.Shop;
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

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;


@SpringBootTest(classes = {KakaopayService.class, PaymentServiceConfig.class})
@EnableConfigurationProperties(PaymentServiceConfig.KakaoPayProperties.class)
@TestPropertySource(locations = "classpath:application.yml")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KakaopayServiceTest {

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private KakaopayService kakaopayService;

    @Mock
    private Shop mockShop;

    @Mock
    private OAuth2Account mockAccount;

    @Mock
    private Address mockAddress;

    private static final String ACCOUNT_EMAIL = "testAccount@email.com";

    private static final String PRODUCT_NAME = "testProduct";
    private static final int REMAINING_QUANTITY = 100;

    private static final String ORDER_NUMBER = UUID.randomUUID().toString();
    private static final int ORDERED_QUANTITY = 2;

    private Order newOrder;
    private Product product;

    @BeforeEach
    void init() {
        product = Product.builder()
                .shop(mockShop)
                .name(PRODUCT_NAME)
                .type(ProductType.ANALOGUE_LIMITATION_EXCLUSIVE)
                .topCategory(ProductTopCategory.MOVIE)
                .stockStatus(ProductStockStatus.ON_SALES)
                .remainingQuantity(REMAINING_QUANTITY)
                .purchaseMaximumQuantity(5)
                .originalPrice(10000)
                .build();

        newOrder = Order.builder()
                .account(mockAccount)
                .product(product)
                .deliveryAddress(mockAddress)
                .orderedQuantity(ORDERED_QUANTITY)
                .orderNumber(ORDER_NUMBER)
                .originalPrice(10000)
                .discountedPrice(10000)
                .build();

        newOrder.ready();

        when(mockAccount.getEmail())
                .thenReturn(ACCOUNT_EMAIL);
    }

    @Test
    void 결제_준비_성공() {
        when(shopRepository.findById(anyLong()))
                .thenReturn(Optional.of(mockShop));

        when(accountRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(mockAccount));

        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(newOrder));

        PaymentService.ReadyResponse ready = kakaopayService.ready(PaymentService.UserAgent.DESKTOP,
                1L, mockAccount.getEmail(), 1L, newOrder.getDiscountedPrice());

        System.out.println(ready);
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