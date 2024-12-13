package co.ohmygoods.payment.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.model.entity.Order;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.payment.exception.PaymentException;
import co.ohmygoods.payment.vo.PaymentStatus;
import co.ohmygoods.payment.vo.ExternalPaymentVendor;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private OAuth2Account buyer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ExternalPaymentVendor externalPaymentVendor;

    @Column(nullable = false, updatable = false)
    private int totalPrice;

    private String transactionId;

    private LocalDateTime transactionEndedAt;

    private LocalDateTime transactionReadyAt;

    public static Payment create(Shop shop, OAuth2Account buyer, Order order, ExternalPaymentVendor vendor, int totalPrice) {
        if (totalPrice < 0) {
            PaymentException.throwCauseInvalidPaymentPrice(totalPrice);
        }

        if (!order.isReady()) {
            PaymentException.throwCauseInvalidOrderStatus(order.getStatus());
        }

        return new Payment(0L, shop, buyer, order, PaymentStatus.PAYMENT_START, vendor,
                totalPrice, null, null, null);
    }

    public void ready(String transactionId, LocalDateTime transactionReadyAt) {
        this.transactionId = transactionId;
        this.transactionReadyAt = transactionReadyAt;
        status = PaymentStatus.PAYMENT_READY;
    }

    public void cancel() {
        transactionEndedAt = LocalDateTime.now();
        status = PaymentStatus.PAYMENT_CANCEL;

        order.cancel();
    }

    public void fail(PaymentStatus cause) {
        transactionEndedAt = LocalDateTime.now();
        status = cause;

        order.fail(OrderStatus.valueOf(cause.name()));
    }

    public void succeed() {
        transactionEndedAt = LocalDateTime.now();
        status = PaymentStatus.PAID;

        order.ordered();
    }
}
