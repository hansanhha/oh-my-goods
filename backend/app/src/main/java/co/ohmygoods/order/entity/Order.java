package co.ohmygoods.order.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.vo.OrderStatus;
import co.ohmygoods.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address deliveryAddress;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private String orderNumber;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false)
    private int discountedPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PAYING;

    private LocalDateTime deliveredAt;

    public void updateOrderStatus(OrderStatus orderStatus) {
        if (!this.orderStatus.isUpdatableStatus()) {
            OrderException.throwCauseCannotUpdateStatus(orderStatus);
        }

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            deliveredAt = LocalDateTime.now();
        }

        this.orderStatus = orderStatus;
    }

    public void updatePurchaseQuantity(int quantity) {
        if (quantity <= 0 || getProduct().isInvalidRequestQuantity(quantity)) {
            OrderException.throwCauseInvalidPurchaseQuantity(quantity);
        }

        if (isCannotUpdateOrder(orderStatus)) {
            OrderException.throwCauseInvalidOrderStatus(orderStatus);
        }

        this.quantity = quantity;
    }

    public void updateDeliveryAddress(Address deliveryAddress) {
        if (isCannotUpdateOrder(orderStatus)) {
            OrderException.throwCauseInvalidOrderStatus(orderStatus);
        }

        this.deliveryAddress = deliveryAddress;
    }

    public static boolean isCannotUpdateOrder(OrderStatus orderStatus) {
        return !orderStatus.equals(OrderStatus.ORDERED) && !orderStatus.equals(OrderStatus.PACKAGING);
    }
}
