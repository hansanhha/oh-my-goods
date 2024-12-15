package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.payment.vo.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private OAuth2Account account;

    private OrderStatus entireOrderStatus;

    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    private int totalPrice;

    private int discountPrice;

    public static Order start(OAuth2Account account, List<OrderItem> orderItems, int totalPrice, int discountPrice) {
        List<OrderItem> orderItems_ = Objects.requireNonNullElseGet(orderItems, Collections::emptyList);
        int totalPrice_ = Math.max(totalPrice, 0);
        int discountPrice_ = Math.max(discountPrice, 0);

        return new Order(0L, account, OrderStatus.ORDER_START, null, orderItems_, totalPrice_, discountPrice_);
    }

    public void updateEntireOrderStatus(OrderStatus orderStatus) {
        if (entireOrderStatus.isNotUpdatableOrderStatus()) {
            OrderException.throwCauseCannotUpdateStatus(orderStatus);
        }

        entireOrderStatus = orderStatus;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public boolean isOrderer(OAuth2Account account) {
        return this.account.getEmail().equals(account.getEmail());
    }
}
