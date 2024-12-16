package co.ohmygoods.order.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.service.CouponService;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.DeliveryAddress;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.order.repository.DeliveryAddressRepository;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.order.service.dto.OrderStartRequest;
import co.ohmygoods.order.service.dto.OrderStartResponse;
import co.ohmygoods.payment.entity.vo.UserAgent;
import co.ohmygoods.payment.service.dto.PreparePaymentRequest;
import co.ohmygoods.payment.service.dto.PaymentStartResponse;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleOrderStartService implements OrderStartService {

    private final CouponService couponService;
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final CouponRepository couponRepository;

    /*
        주문 시 재고 차감 X
        결제 및 주문 완료 시점으로 재고 차감 미룸
     */
    @Override
    public OrderStartResponse startOrder(OrderStartRequest request) {
        // 엔티티 조회
        OAuth2Account account = accountRepository.findByEmail(request.orderAccountEmail()).orElseThrow(OrderException::new);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.deliveryAddressId()).orElseThrow(OrderException::new);
        List<Product> orderProducts = (List<Product>) productRepository.findAllById(request.orderDetails().stream()
                .map(OrderStartRequest.OrderProductDetail::productId).toList());

        // product 엔티티에 해당하는 request dto 매핑
        Map<Product, OrderStartRequest.OrderProductDetail> orderProductDetailMap =
                orderProducts.stream()
                        .collect(Collectors.toMap(product -> product, product -> request.orderDetails()
                                .stream()
                                .filter(detail -> detail.productId().equals(product.getId()))
                                .findFirst()
                                .orElseThrow(OrderException::new)));

        // 매핑된 정보를 기반으로 주문 아이템 엔티티 생성
        // 구매 개수 검증 (Product.isValidRequestQuantity(int))
        List<OrderItem> orderItems = orderProductDetailMap
                .entrySet()
                .stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    OrderStartRequest.OrderProductDetail orderDetail = entry.getValue();

                    if (product.isValidRequestQuantity(orderDetail.purchaseQuantity())) {
                        throw new OrderException();
                    }

                    int originalPrice = product.getOriginalPrice();
                    int totalDiscountedPrice = 0;
                    int productDiscountedPrice = 0;
                    int couponDiscountedPrice = 0;
                    int productFinalPrice = originalPrice;

                    // 상품 할인 적용
                    if (product.getDiscountRate() > 0) {
                        productDiscountedPrice = getDiscountPriceByProductDiscountRate(originalPrice, product.getDiscountRate());
                    }

                    // 쿠폰 적용
                    if (orderDetail.isAppliedCoupon()) {
                        couponDiscountedPrice = couponService.applyCoupon(account.getEmail(),
                                orderDetail.appliedCouponId(), productFinalPrice);
                    }

                    totalDiscountedPrice += (productDiscountedPrice + couponDiscountedPrice);
                    productFinalPrice -= totalDiscountedPrice;

                    return OrderItem.builder()
                            .product(product)
                            .deliveryAddress(deliveryAddress)
                            .orderQuantity(orderDetail.purchaseQuantity())
                            .orderNumber(generateOrderNumber())
                            .originalPrice(originalPrice)
                            .couponDiscountPrice(couponDiscountedPrice)
                            .productDiscountPrice(productDiscountedPrice)
                            .purchasePrice(productFinalPrice)
                            .build();
                })
                .toList();

        // 각 주문 아이템에 대한 최종 구매 금액/할인 금액(쿠폰+상품) 합계
        // Atomic 대신 Stream으로 전체 주문 아이템 생성 후 일괄 처리
        final String TOTAL_PURCHASE_PRICE = "totalPrice";
        final String TOTAL_COUPON_DISCOUNT_PRICE = "totalCouponDiscountPrice";
        final String TOTAL_PRODUCT_DISCOUNT_PRICE = "totalProductDiscountPrice";

        HashMap<String, Integer> summarizingPriceMap = orderItems.stream()
                .collect(() -> new HashMap<>(Map.of(
                                TOTAL_PURCHASE_PRICE, 0,
                                TOTAL_COUPON_DISCOUNT_PRICE, 0,
                                TOTAL_PRODUCT_DISCOUNT_PRICE, 0)),
                        (map, orderItem) -> {
                            map.put(TOTAL_PURCHASE_PRICE, map.get(TOTAL_PURCHASE_PRICE) + orderItem.getPurchasePrice());
                            map.put(TOTAL_COUPON_DISCOUNT_PRICE, map.get(TOTAL_COUPON_DISCOUNT_PRICE) + orderItem.getCouponDiscountPrice());
                            map.put(TOTAL_PRODUCT_DISCOUNT_PRICE, map.get(TOTAL_PRODUCT_DISCOUNT_PRICE) + orderItem.getProductDiscountPrice());
                        },
                        (map1, map2) -> {
                            map1.put(TOTAL_PURCHASE_PRICE, map1.get(TOTAL_PURCHASE_PRICE) + map2.get(TOTAL_PURCHASE_PRICE));
                            map1.put(TOTAL_COUPON_DISCOUNT_PRICE, map1.get(TOTAL_COUPON_DISCOUNT_PRICE) + map2.get(TOTAL_COUPON_DISCOUNT_PRICE));
                            map1.put(TOTAL_PRODUCT_DISCOUNT_PRICE, map1.get(TOTAL_PRODUCT_DISCOUNT_PRICE) + map2.get(TOTAL_PRODUCT_DISCOUNT_PRICE));
                        });

        // 주문 엔티티 생성
        Order newOrder = Order.start(account, UUID.randomUUID().toString(), orderItems,
                summarizingPriceMap.get(TOTAL_PURCHASE_PRICE),
                summarizingPriceMap.get(TOTAL_COUPON_DISCOUNT_PRICE) +
                        summarizingPriceMap.get(TOTAL_PRODUCT_DISCOUNT_PRICE));

        Order order = orderRepository.save(newOrder);

        PreparePaymentRequest preparePaymentRequest = new PreparePaymentRequest(request.orderPaymentMethod(),
                UserAgent.DESKTOP, account.getEmail(), order.getId(), order.getTransactionId(), order.getTotalPrice(), generatePaymentName(order));

        // 결제 준비 요청(외부 api 호출)
        PaymentStartResponse paymentStartResponse = paymentGateway.startPayment(preparePaymentRequest);

        // 결제 준비 요청 결과에 따른 분기 처리
        if (!paymentStartResponse.isStartSuccess()) {
            order.fail(OrderStatus.ORDER_FAILED_PAYMENT_FAILURE, paymentStartResponse.paymentFailureCause());
            return OrderStartResponse.fail(paymentStartResponse.paymentFailureCause().getMessage());
        }

        return OrderStartResponse.success(paymentStartResponse.redirectUrl(),
                order.getTransactionId(), order.getCreatedAt());
    }

    private int getDiscountPriceByProductDiscountRate(int originalPrice, int discountRate) {
        double discountPrice = originalPrice - (originalPrice * (double) discountRate / 100);
        BigDecimal halfUpDiscountPrice = BigDecimal.valueOf(discountPrice).setScale(0, RoundingMode.HALF_UP);
        return halfUpDiscountPrice.intValue();
    }

    private String generatePaymentName(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        int orderItemSize = orderItems.size();

        if (!orderItems.isEmpty()) {
            return orderItems.getFirst().getProduct().getName()
                    .concat("외 ")
                    .concat(String.valueOf(orderItemSize))
                    .concat("개 상품 결제");
        }

        return "oh-my-goods 결제";
    }

}
