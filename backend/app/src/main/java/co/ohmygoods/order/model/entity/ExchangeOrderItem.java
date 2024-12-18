package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.ExchangeException;
import co.ohmygoods.order.model.vo.ExchangeStatus;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.model.entity.Shop;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_target_order_item_id")
    private OrderItem exchangeTargetOrderItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private OAuth2Account manager;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_product_id")
    private Product exchangeProduct;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus status;

    @Column(nullable = false)
    private String requestReason;

    private String requestResponse;

    private LocalDateTime respondedAt;

    public void updateRequestReason(String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            ExchangeException.throwCauseEmptyText();
        }

        this.requestReason = requestReason;
    }

    public void updateRequestResponse(String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            ExchangeException.throwCauseEmptyText();
        }

        this.requestResponse = requestResponse;
    }

    public static ExchangeOrderItem requestByBuyer(Shop shop, OrderItem requestOrderItem, String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            ExchangeException.throwCauseEmptyText();
        }

        return new ExchangeOrderItem(0L, requestOrderItem, shop, null, null,
                ExchangeStatus.REQUESTED_EXCHANGING, requestReason, null, null);
    }

    public void approveByShopManager(OAuth2Account manager, Product exchangeProduct, String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            ExchangeException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.exchangeProduct = exchangeProduct;
        this.requestResponse = requestResponse;
        respondedAt = LocalDateTime.now();
        status = ExchangeStatus.EXCHANGED;
        exchangeTargetOrderItem.updateOrderItemStatus(OrderStatus.ORDER_ITEM_EXCHANGED);
    }

    public void rejectByShopManager(OAuth2Account manager, String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            ExchangeException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.requestResponse = requestResponse;
        respondedAt = LocalDateTime.now();
        status = ExchangeStatus.REJECTED_EXCHANGING;
        exchangeTargetOrderItem.updateOrderItemStatus(OrderStatus.ORDER_ITEM_REJECTED_EXCHANGING);
    }
}
