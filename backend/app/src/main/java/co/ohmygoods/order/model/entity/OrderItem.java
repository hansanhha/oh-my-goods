package co.ohmygoods.order.model.entity;

import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.product.model.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private DeliveryAddress deliveryAddress;

    @OneToOne(mappedBy = "orderItem", orphanRemoval = true)
    private CouponUsageHistory couponUsageHistory;

    @Column(nullable = false)
    private int orderQuantity;

    @Column(nullable = false, updatable = false)
    private String orderNumber;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false, updatable = false)
    private int couponDiscountPrice;

    @Column(nullable = false, updatable = false)
    private int productDiscountPrice;

    @Column(nullable = false, updatable = false)
    private int purchasePrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDER_START;

    private LocalDateTime deliveredAt;

    public void updateOrderItemStatus(OrderStatus orderStatus) {
        if (orderStatus.isNotUpdatableOrderStatus()) {
            OrderException.throwCauseCannotUpdateStatus(orderStatus);
        }

        if (orderStatus.equals(OrderStatus.ORDER_ITEM_DELIVERED)) {
            deliveredAt = LocalDateTime.now();
        }

        this.orderStatus = orderStatus;
    }

    public void updateDeliveryAddress(DeliveryAddress deliveryAddress) {
        if (orderStatus.isNotUpdatableOrderStatus()) {
            OrderException.throwCauseInvalidOrderStatus(orderStatus);
        }

        this.deliveryAddress = deliveryAddress;
    }

    public void updatePurchaseQuantity(int quantity) {
        if (quantity <= 0 || getProduct().isValidRequestQuantity(quantity)) {
            OrderException.throwCauseInvalidPurchaseQuantity(quantity);
        }

        if (!orderStatus.isNotUpdatableOrderStatus()) {
            OrderException.throwCauseInvalidOrderStatus(orderStatus);
        }

        this.orderQuantity = quantity;
    }

    public void updateCouponApplyingPurchasePrice(int productFinalPrice, int couponDiscountedPrice) {
        if (productFinalPrice < 0 || couponDiscountedPrice < 0)
            throw new OrderException();

        this.purchasePrice = productFinalPrice;
        this.couponDiscountPrice = couponDiscountedPrice;
    }
}
