package co.ohmygoods.order.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.service.CouponService;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.DeliveryAddress;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.DeliveryAddressRepository;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.order.service.dto.OrderStartRequest;
import co.ohmygoods.order.service.dto.OrderStartResponse;
import co.ohmygoods.payment.service.PaymentGateway;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SimpleOrderStartService implements OrderStartService {

    private final CouponService couponService;
    private final PaymentGateway paymentGateway;
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
        OAuth2Account account = accountRepository.findByEmail(request.orderAccountEmail()).orElseThrow(OrderException::new);
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.deliveryAddressId()).orElseThrow(OrderException::new);
        List<Product> orderProducts = (List<Product>) productRepository.findAllById(request.orderDetails().stream()
                .map(OrderStartRequest.OrderProductDetail::productId).toList());

        Map<Product, OrderStartRequest.OrderProductDetail> orderProductDetailMap = orderProducts.stream()
                .collect(Collectors.toMap(product -> product, product -> request.orderDetails()
                        .stream()
                        .filter(detail -> detail.productId().equals(product.getId()))
                        .findFirst()
                        .orElseThrow(OrderException::new)));

        List<OrderItem> orderItems = orderProductDetailMap
                .entrySet()
                .stream()
                .map(entry -> {
                    Product product = entry.getKey();
                    OrderStartRequest.OrderProductDetail orderDetail = entry.getValue();

                    int originalPrice = product.getOriginalPrice();
                    int totalDiscountedPrice = 0;
                    int productDiscountedPrice = 0;
                    int couponDiscountedPrice = 0;
                    int productFinalPrice = originalPrice;

                    if (product.getDiscountRate() > 0) {
                        productDiscountedPrice = getDiscountPriceByProductDiscountRate(originalPrice, product.getDiscountRate());
                    }

                    if (orderDetail.isAppliedCoupon()) {
                        couponDiscountedPrice = couponService.applyCoupon(account.getEmail(),
                                orderDetail.appliedCouponId(), productFinalPrice);
                    }

                    totalDiscountedPrice += (productDiscountedPrice + couponDiscountedPrice);
                    productFinalPrice -= totalDiscountedPrice;

                    return OrderItem.builder()
                            .account(account)
                            .product(product)
                            .deliveryAddress(deliveryAddress)
                            .orderedQuantity(orderDetail.purchaseQuantity())
                            .orderNumber(generateOrderNumber())
                            .originalPrice(originalPrice)
                            .couponDiscountedPrice(couponDiscountedPrice)
                            .productDiscountedPrice(productDiscountedPrice)
                            .totalDiscountedPrice(totalDiscountedPrice)
                            .purchasePrice(productFinalPrice)
                            .build();
                })
                .toList();

        orderItemRepository.saveAll(orderItems);


    }

    private int getDiscountPriceByProductDiscountRate(int originalPrice, int discountRate) {
        double discountPrice = originalPrice - (originalPrice * (double) discountRate / 100);
        BigDecimal halfUpDiscountPrice = BigDecimal.valueOf(discountPrice).setScale(0, RoundingMode.HALF_UP);
        return halfUpDiscountPrice.intValue();
    }

}
