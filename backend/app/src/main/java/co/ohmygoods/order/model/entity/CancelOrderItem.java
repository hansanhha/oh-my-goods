package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.CancelOrderException;
import co.ohmygoods.order.model.vo.CancelOrderStatus;
import co.ohmygoods.order.model.vo.OrderStatus;
import co.ohmygoods.shop.entity.Shop;
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
public class CancelOrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancel_target_order_item_id")
    private OrderItem cancelTargetOrderItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private OAuth2Account manager;

    @Enumerated(EnumType.STRING)
    private CancelOrderStatus status;

    private String requestReason;

    private String requestResponse;

    private LocalDateTime respondedAt;

    public void updateRequestReason(String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            CancelOrderException.throwCauseEmptyText();
        }

        this.requestReason = requestReason;
    }

    public void updateRequestResponse(String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            CancelOrderException.throwCauseEmptyText();
        }

        this.requestResponse = requestResponse;
    }

    public static CancelOrderItem requestByBuyer(Shop shop, OrderItem requestOrderItem, String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            CancelOrderException.throwCauseEmptyText();
        }

        return new CancelOrderItem(0L, requestOrderItem,shop, null,
                CancelOrderStatus.REQUESTED_CANCEL_ORDER, requestReason, null, null);
    }

    public static CancelOrderItem forceCancelByShopManager(Shop shop, OrderItem cancelTargetOrderItem, OAuth2Account manager, String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            CancelOrderException.throwCauseEmptyText();
        }

        return new CancelOrderItem(0L, cancelTargetOrderItem, shop, manager,
                CancelOrderStatus.CANCELED_ORDER, null, requestResponse, LocalDateTime.now());
    }

    public void approveByShopManager(OAuth2Account manager, String requestResponse, CancelOrderStatus status) {
        if (!StringUtils.hasText(requestResponse)) {
            CancelOrderException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.requestReason = requestResponse;
        this.status = status;
        cancelTargetOrderItem.updateOrderItemStatus(OrderStatus.valueOf(status.name()));
        respondedAt = LocalDateTime.now();
    }

    public void rejectByShopManager(OAuth2Account manager, String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            CancelOrderException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.requestReason = requestResponse;
        status = CancelOrderStatus.REJECTED_CANCEL_ORDER;
        cancelTargetOrderItem.updateOrderItemStatus(OrderStatus.ORDER_ITEM_REJECTED_CANCEL_ORDER);
        respondedAt = LocalDateTime.now();
    }
}