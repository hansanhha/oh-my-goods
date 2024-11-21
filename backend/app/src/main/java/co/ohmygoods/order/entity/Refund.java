package co.ohmygoods.order.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.RefundException;
import co.ohmygoods.order.vo.OrderStatus;
import co.ohmygoods.order.vo.RefundStatus;
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
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_target_order_id")
    private Order refundTargetOrder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private OAuth2Account manager;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @Column(nullable = false)
    private String requestReason;

    private String requestResponse;

    private int refundPrice;

    private LocalDateTime respondedAt;

    public void updateRequestReason(String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            RefundException.throwCauseEmptyText();
        }

        this.requestReason = requestReason;
    }

    public void updateRequestResponse(String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            RefundException.throwCauseEmptyText();
        }

        this.requestResponse = requestResponse;
    }

    public static Refund requestByBuyer(Shop shop, Order requestOrder, String requestReason) {
        if (!StringUtils.hasText(requestReason)) {
            RefundException.throwCauseEmptyText();
        }

        return new Refund(0L, requestOrder, shop, null,
                RefundStatus.REQUESTED_REFUNDING, requestReason, null, 0, null);
    }

    public void approveByShopManager(OAuth2Account manager, String requestResponse, int refundedPrice) {
        if (!StringUtils.hasText(requestResponse)) {
            RefundException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.requestResponse = requestResponse;
        this.refundPrice = refundedPrice;
        respondedAt = LocalDateTime.now();
        status = RefundStatus.APPROVED_REFUNDING;
        refundTargetOrder.updateOrderStatus(OrderStatus.APPROVED_REFUNDING);
    }

    public void rejectByShopManager(OAuth2Account manager, String requestResponse) {
        if (!StringUtils.hasText(requestResponse)) {
            RefundException.throwCauseEmptyText();
        }

        this.manager = manager;
        this.requestResponse = requestResponse;
        respondedAt = LocalDateTime.now();
        status = RefundStatus.REJECTED_REFUNDING;
        refundTargetOrder.updateOrderStatus(OrderStatus.REJECTED_REFUNDING);
    }
}
