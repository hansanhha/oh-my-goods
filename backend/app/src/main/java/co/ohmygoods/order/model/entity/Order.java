package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.exception.ProductStockStatusException;
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
    private int orderedQuantity;

    @Column(nullable = false, updatable = false)
    private String orderNumber;

    @Column(nullable = false)
    private int originalPrice;

    @Column(nullable = false)
    private int discountedPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus status = OrderStatus.PAYING;

    private LocalDateTime deliveredAt;

    public void updateOrderStatus(OrderStatus orderStatus) {
        if (!this.status.isUpdatableStatus()) {
            OrderException.throwCauseCannotUpdateStatus(orderStatus);
        }

        if (orderStatus.equals(OrderStatus.DELIVERED)) {
            deliveredAt = LocalDateTime.now();
        }

        this.status = orderStatus;
    }

    public void updatePurchaseQuantity(int quantity) {
        if (quantity <= 0 || getProduct().isInvalidRequestQuantity(quantity)) {
            OrderException.throwCauseInvalidPurchaseQuantity(quantity);
        }

        if (isCannotUpdateOrder(status)) {
            OrderException.throwCauseInvalidOrderStatus(status);
        }

        this.orderedQuantity = quantity;
    }

    public void updateDeliveryAddress(Address deliveryAddress) {
        if (isCannotUpdateOrder(status)) {
            OrderException.throwCauseInvalidOrderStatus(status);
        }

        this.deliveryAddress = deliveryAddress;
    }

    public static boolean isCannotUpdateOrder(OrderStatus orderStatus) {
        return !orderStatus.equals(OrderStatus.ORDERED) && !orderStatus.equals(OrderStatus.PACKAGING);
    }

    public void ordered() {
        status = OrderStatus.ORDERED;
    }

    public boolean isReady() {
        return status.equals(OrderStatus.ORDER_READY);
    }

    public void ready() {
        try {
            product.decrease(orderedQuantity);
            status = OrderStatus.ORDER_READY;
        } catch (ProductException e) {
            status = OrderStatus.ORDER_FAILED_LACK_QUANTITY;
            OrderException.throwCauseInvalidPurchaseQuantity(orderedQuantity);
        } catch (ProductStockStatusException e) {
            status = OrderStatus.ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS;
            OrderException.throwCauseInvalidProductStockStatus(product.getStockStatus().getMessage());
        }
    }

    public void cancel() {
        product.increase(orderedQuantity);
        status = OrderStatus.CANCELED_ORDER;
    }

    public void fail(OrderStatus cause) {
        product.increase(orderedQuantity);
        status = cause;
    }

    public boolean isOrderer(OAuth2Account account) {
        return this.account.getEmail().equals(account.getEmail());
    }
}
