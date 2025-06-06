package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.payment.model.entity.Payment;
import co.ohmygoods.payment.model.vo.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String transactionId;

    private OrderStatus entireOrderStatus;

    private PaymentStatus paymentStatus;

    @OneToOne(mappedBy = "order", orphanRemoval = true)
    private Payment payment;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private int totalPrice;

    private int discountPrice;

    public static Order start(Account account, String transactionId, List<OrderItem> orderItems, int totalPrice, int discountPrice) {
        List<OrderItem> orderItems_ = Objects.requireNonNullElseGet(orderItems, Collections::emptyList);
        int totalPrice_ = Math.max(totalPrice, 0);
        int discountPrice_ = Math.max(discountPrice, 0);

        return new Order(0L, account, transactionId, OrderStatus.ORDER_START, null, null, orderItems_, totalPrice_, discountPrice_);
    }

    public void ordered() {
        entireOrderStatus = OrderStatus.ORDERED;
        paymentStatus = PaymentStatus.PAID;

        this.orderItems.forEach(oi -> oi.updateOrderItemStatus(OrderStatus.ORDERED));
    }

    public void cancel() {
        entireOrderStatus = OrderStatus.ORDER_FAILED_PAYMENT_CANCEL;
        paymentStatus = PaymentStatus.PAYMENT_CANCEL;

        this.orderItems.forEach(oi -> oi.updateOrderItemStatus(OrderStatus.ORDER_FAILED_PAYMENT_CANCEL));
    }

    public void fail(OrderStatus orderStatus, PaymentStatus paymentStatus) {
        entireOrderStatus = orderStatus;
        this.paymentStatus = paymentStatus;

        this.orderItems.forEach(oi -> oi.updateOrderItemStatus(OrderStatus.ORDER_FAILED_PAYMENT_FAILURE));
    }

    public void updateEntireOrderStatus(OrderStatus orderStatus) {
        if (entireOrderStatus.isNotUpdatableOrderStatus()) {
            throw OrderException.CANNOT_UPDATE_ORDER_STATUS;
        }

        entireOrderStatus = orderStatus;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public boolean isOrderer(Account account) {
        return this.account.getMemberId().equals(account.getMemberId());
    }
}
