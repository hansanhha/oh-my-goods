package co.ohmygoods.order.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.coupon.model.entity.CouponUsingHistory;
import co.ohmygoods.coupon.service.user.CouponService;
import co.ohmygoods.order.exception.DeliveryAddressException;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.DeliveryAddress;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.order.repository.DeliveryAddressRepository;
import co.ohmygoods.order.repository.OrderRepository;
import co.ohmygoods.order.service.dto.OrderCheckoutRequest;
import co.ohmygoods.order.service.dto.OrderCheckoutResponse;
import co.ohmygoods.payment.model.event.PaymentCancelEvent;
import co.ohmygoods.payment.model.event.PaymentFailureEvent;
import co.ohmygoods.payment.model.event.PaymentSuccessEvent;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import co.ohmygoods.payment.model.vo.UserAgent;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.payment.service.dto.PaymentPrepareAPIRequest;
import co.ohmygoods.payment.service.dto.PaymentStartResult;
import co.ohmygoods.product.exception.ProductError;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class DefaultOrderProcessingService implements OrderProcessingService {

    private final CouponService couponService;
    private final PaymentGateway paymentGateway;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    /**
     * 재고 차감은 주문 시점이 아닌 결제 및 주문 완료 시점에 이루어진다
     * <p>따라서 checkout 메서드에서 재고 차감과 관련된 검증 작업을 수행하지 않는다</p>
     */
    @Override
    public OrderCheckoutResponse checkout(OrderCheckoutRequest request) {
        // 엔티티 조회
        Account account = accountRepository.findByEmail(request.orderAccountEmail()).orElseThrow(AuthException::notFoundAccount);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.deliveryAddressId()).orElseThrow(DeliveryAddressException::notFoundDeliveryAddress);
        List<Product> orderProducts = (List<Product>) productRepository.findAllById(request.orderDetails().stream()
                .map(OrderCheckoutRequest.OrderProductDetail::productId).toList());

        // Product 엔티티와 상품 주문 상세 DTO 매핑
        Map<Product, OrderCheckoutRequest.OrderProductDetail> orderProductDetailMap =
                orderProducts.stream()
                        .collect(Collectors.toMap(product -> product, product -> request.orderDetails()
                                .stream()
                                .filter(detail -> detail.productId().equals(product.getId()))
                                .findFirst()
                                .orElseThrow(OrderException::notFoundOrderItem)));

        // 매핑된 정보를 기반으로 OrderItem 엔티티 생성 (구매 개수 검증)
        List<OrderItem> orderItems = orderProductDetailMap
                .entrySet()
                .stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    OrderCheckoutRequest.OrderProductDetail orderDetail = entry.getValue();

                    if (product.isValidPurchaseQuantity(orderDetail.purchaseQuantity())) {
                        throw OrderException.INVALID_PURCHASE_QUANTITY;
                    }

                    int originalPrice = product.getOriginalPrice();
                    int productDiscountedPrice = 0;
                    int couponDiscountedPrice = 0;
                    int productFinalPrice = originalPrice;

                    // 상품 할인 적용
                    if (product.isDiscounted()) {
                        productDiscountedPrice = product.calculateActualPrice();
                        productFinalPrice = productDiscountedPrice;
                    }

                    OrderItem orderItem = OrderItem.builder()
                            .product(product)
                            .deliveryAddress(deliveryAddress)
                            .orderQuantity(orderDetail.purchaseQuantity())
                            .orderNumber(UUID.randomUUID().toString())
                            .originalPrice(originalPrice)
                            .couponDiscountPrice(couponDiscountedPrice)
                            .productDiscountPrice(productDiscountedPrice)
                            .purchasePrice(productFinalPrice)
                            .build();

                    // 쿠폰 적용
                    if (orderDetail.isUsingCoupon()) {
                        couponDiscountedPrice = couponService.use(account.getMemberId(),
                                orderItem.getId(), orderDetail.couponId(), productDiscountedPrice);

                        orderItem.updatePurchasePriceByCoupon(couponDiscountedPrice);
                    }

                    return orderItem;
                })
                .toList();

        // 모든 OrderItem에 대한 최종 구매 금액과 할인 금액(쿠폰 사용 및 상품 자체 할인) 계산
        AtomicInteger totalPurchasePrice = new AtomicInteger();
        AtomicInteger totalCouponDiscountedPrice = new AtomicInteger();
        AtomicInteger totalProductDiscountedPrice = new AtomicInteger();

        orderItems.forEach(oi -> {
            totalPurchasePrice.addAndGet(oi.getPurchasePrice());
            totalCouponDiscountedPrice.addAndGet(oi.getCouponDiscountPrice());
            totalProductDiscountedPrice.addAndGet(oi.getProductDiscountPrice());
        });

        // 주문 엔티티 생성
        Order newOrder = Order.start(account, UUID.randomUUID().toString(), orderItems,
                totalPurchasePrice.get(), totalCouponDiscountedPrice.get() + totalProductDiscountedPrice.get());

        Order order = orderRepository.save(newOrder);

        PaymentPrepareAPIRequest paymentPrepareAPIRequest = new PaymentPrepareAPIRequest(request.orderPaymentMethod(),
                UserAgent.DESKTOP, account.getEmail(), order.getId(), order.getTransactionId(), order.getTotalPrice(), generatePaymentName(order));

        // 결제 준비 요청 (외부 api 호출)
        PaymentStartResult paymentStartResult = paymentGateway.start(paymentPrepareAPIRequest);

        if (!paymentStartResult.isSuccessful()) {
            order.fail(OrderStatus.ORDER_FAILED_PAYMENT_FAILURE, paymentStartResult.paymentFailureCause());
            return OrderCheckoutResponse.fail(paymentStartResult.paymentFailureCause().getMessage());
        }

        return OrderCheckoutResponse.success(paymentStartResult.nextRedirectUrl(),
                order.getTransactionId(), order.getCreatedAt());
    }

    /**
     * 결제 성공 시 상품의 재고를 구매 수량만큼 차감한다
     * <p>재고 검증에 실패하면 주문 실패로 주문 상태를 변경한다 (추후 결제 금액 환불 처리 수행)</p>
     */
    @EventListener(PaymentSuccessEvent.class)
    @Override
    public void successOrder(PaymentSuccessEvent event) {
        Order order = orderRepository.fetchOrderItemsByPaymentId(event.paymentId()).orElseThrow(OrderException::notFoundOrder);

        order.getOrderItems().forEach(orderItem -> {
            Product product = orderItem.getProduct();
            int orderQuantity = orderItem.getOrderQuantity();

            // 구매 수량만큼 재고 차감
            // 예외 발생 시 원인에 따른 주문 실패 처리(트랜잭션 예외 발생 X) -> 결제 금액 환불 처리 필요
            try {
                product.decrease(orderQuantity);
            } catch (ProductException e) {
                switch (e.getDomainError()) {
                    case ProductError.EXCEED_PURCHASE_PRODUCT_MAX_LIMIT, ProductError.NOT_ENOUGH_STOCK -> order.fail(OrderStatus.ORDER_FAILED_OUT_OF_STOCK, PaymentStatus.PAID);
                    case ProductError.NOT_SALES_STATUS -> order.fail(OrderStatus.ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS, PaymentStatus.PAID);
                    default -> order.fail(OrderStatus.ORDER_FAILED_UNKNOWN, PaymentStatus.PAID);
                }
            }
        });

        order.ordered();
        logOrderResult(order, OrderResult.SUCCESSFUL);
    }

    @EventListener(PaymentCancelEvent.class)
    @Override
    public void cancelOrderByPaymentCancellation(PaymentCancelEvent event) {
        Order order = orderRepository.fetchOrderItemsByPaymentId(event.paymentId()).orElseThrow(OrderException::notFoundOrder);

        List<Long> couponHistoryIds = order.getOrderItems().stream()
                .map(OrderItem::getCouponUsingHistory)
                .filter(Objects::nonNull)
                .map(CouponUsingHistory::getId)
                .toList();

        couponService.restoreUsedCoupon(couponHistoryIds);

        order.cancel();
        logOrderResult(order, OrderResult.CANCELLATION);
    }

    @EventListener(PaymentFailureEvent.class)
    @Override
    public void failOrderByPaymentFailed(PaymentFailureEvent event) {
        Order order = orderRepository.fetchOrderItemsByPaymentId(event.paymentId()).orElseThrow(OrderException::notFoundOrder);

        List<Long> couponHistoryIds = order.getOrderItems().stream()
                .map(OrderItem::getCouponUsingHistory)
                .filter(Objects::nonNull)
                .map(CouponUsingHistory::getId)
                .toList();

        couponService.restoreUsedCoupon(couponHistoryIds);

        order.fail(OrderStatus.ORDER_FAILED_PAYMENT_FAILURE, event.paymentFailureCause());
        logOrderResult(order, OrderResult.FAILURE);
    }

    private String generatePaymentName(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        int orderItemSize = orderItems.size();

        if (orderItemSize > 1) {
            return orderItems.getFirst().getProduct().getName()
                    .concat(" 외 ")
                    .concat(String.valueOf(orderItemSize-1))
                    .concat("개 상품 결제");
        }

        return orderItems.getFirst().getProduct().getName() + " 결제";
    }

    private void logOrderResult(Order order, OrderResult result) {
        String orderItemsMessage = createOrderItemsMessage(order.getOrderItems());
        String orderMessage = createOrderMessage(order, orderItemsMessage);
        String resultMessage = result.createResultMessage(orderMessage);

        log.info(resultMessage);
    }

    private String createOrderItemsMessage(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> String.format("%s %d개", orderItem.getProduct().getName(), orderItem.getOrderQuantity()))
                .collect(Collectors.joining(", "));
    }


    private String createOrderMessage(Order order, String orderItemsInfo) {
        return """
            account email: %s
            payment status: %s
            orderTransactionId: %s
            totalPrice: %d
            discountPrice: %d
            purchasePrice: %d
            orderItems: %s
            """.formatted(
                order.getAccount().getEmail(),
                order.getPayment().getStatus(),
                order.getTransactionId(),
                order.getTotalPrice(),
                order.getDiscountPrice(),
                (order.getTotalPrice() - order.getDiscountPrice()),
                orderItemsInfo
        );
    }

    @RequiredArgsConstructor
    private enum OrderResult {
        SUCCESSFUL("order succeed. "),
        FAILURE("order failed. "),
        CANCELLATION("order canceled");

        private final String message;

        private String createResultMessage(String orderInfoMessage) {
            return "%s %s".formatted(message, orderInfoMessage);
        }
    }
}
