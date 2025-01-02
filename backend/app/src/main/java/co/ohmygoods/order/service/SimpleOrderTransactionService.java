package co.ohmygoods.order.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
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
import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.payment.service.dto.PaymentStartResponse;
import co.ohmygoods.payment.service.dto.PreparePaymentRequest;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.exception.ProductStockStatusException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleOrderTransactionService implements OrderTransactionService {

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
    public OrderCheckoutResponse checkout(OrderCheckoutRequest request) {
        // 엔티티 조회
        Account account = accountRepository.findByEmail(request.orderAccountEmail()).orElseThrow(OrderException::new);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.deliveryAddressId()).orElseThrow(OrderException::new);
        List<Product> orderProducts = (List<Product>) productRepository.findAllById(request.orderDetails().stream()
                .map(OrderCheckoutRequest.OrderProductDetail::productId).toList());

        // product 엔티티에 해당하는 request dto 매핑
        Map<Product, OrderCheckoutRequest.OrderProductDetail> orderProductDetailMap =
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
                    OrderCheckoutRequest.OrderProductDetail orderDetail = entry.getValue();

                    if (product.isValidRequestQuantity(orderDetail.purchaseQuantity())) {
                        throw new OrderException();
                    }

                    int originalPrice = product.getOriginalPrice();
                    int productDiscountedPrice = 0;
                    int couponDiscountedPrice = 0;
                    int productFinalPrice = originalPrice;

                    // 상품 할인 적용
                    if (product.getDiscountRate() > 0) {
                        productDiscountedPrice = product.calculateActualPrice();
                        productFinalPrice = productDiscountedPrice;
                    }

                    OrderItem orderItem = OrderItem.builder()
                            .product(product)
                            .deliveryAddress(deliveryAddress)
                            .orderQuantity(orderDetail.purchaseQuantity())
                            .orderNumber(generateOrderNumber())
                            .originalPrice(originalPrice)
                            .couponDiscountPrice(couponDiscountedPrice)
                            .productDiscountPrice(productDiscountedPrice)
                            .purchasePrice(productFinalPrice)
                            .build();

                    // 쿠폰 적용
                    if (orderDetail.isAppliedCoupon()) {
                        couponDiscountedPrice = couponService.applyCoupon(account.getEmail(),
                                orderItem.getId(), orderDetail.appliedCouponId(), productDiscountedPrice);

                        productFinalPrice -= couponDiscountedPrice;
                        orderItem.updateCouponApplyingPurchasePrice(productFinalPrice, couponDiscountedPrice);
                    }

                    return orderItem;
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
            return OrderCheckoutResponse.fail(paymentStartResponse.paymentFailureCause().getMessage());
        }

        return OrderCheckoutResponse.success(paymentStartResponse.nextRedirectUrl(),
                order.getTransactionId(), order.getCreatedAt());
    }

    @Override
    public void successOrder(Long orderId) {
        Order order = orderRepository.fetchOrderItemsAndProductById(orderId).orElseThrow(OrderException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            int orderQuantity = orderItem.getOrderQuantity();

            // 구매 수량만큼 재고 차감
            // 예외 발생 시 주문 실패 처리(트랜잭션 예외 발생 X) -> 결제 금액 환불 처리 필요
            try {
                product.decrease(orderQuantity);
            } catch (ProductException e) {
                order.fail(OrderStatus.ORDER_FAILED_OUT_OF_STOCK, PaymentStatus.PAID);
                return;
            } catch (ProductStockStatusException e) {
                order.fail(OrderStatus.ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS, PaymentStatus.PAID);
                return;
            }
        }

        order.ordered();
    }

    @Override
    public void cancelOrderByPaymentCancellation(Long orderId) {
        Order order = orderRepository.fetchOrderItemsAndProductById(orderId).orElseThrow(OrderException::new);

        List<Long> couponUsageHistoryIds = order.getOrderItems().stream()
                .map(OrderItem::getCouponUsageHistory)
                .filter(Objects::nonNull)
                .map(CouponUsageHistory::getId)
                .toList();

        couponService.restoreAppliedCoupon(order.getAccount().getEmail(), couponUsageHistoryIds);

        order.cancel();
    }

    @Override
    public void failOrderByPaymentFailed(Long orderId, PaymentStatus paymentFailureCause) {
        Order order = orderRepository.fetchOrderItemsAndProductById(orderId).orElseThrow(OrderException::new);

        List<Long> couponUsageHistoryIds = order.getOrderItems().stream()
                .map(OrderItem::getCouponUsageHistory)
                .filter(Objects::nonNull)
                .map(CouponUsageHistory::getId)
                .toList();

        couponService.restoreAppliedCoupon(order.getAccount().getEmail(), couponUsageHistoryIds);

        order.fail(OrderStatus.ORDER_FAILED_PAYMENT_FAILURE, paymentFailureCause);
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
