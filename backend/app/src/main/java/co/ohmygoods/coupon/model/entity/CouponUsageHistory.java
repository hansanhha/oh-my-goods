package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.model.vo.CouponUsageStatus;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.model.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CouponUsageHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponUsageStatus couponUsageStatus;

    private LocalDateTime usedAt;

    public static CouponUsageHistory issued(Coupon coupon, OAuth2Account account) {
        CouponUsageHistory couponUsageHistory = new CouponUsageHistory();
        couponUsageHistory.coupon = coupon;
        couponUsageHistory.account = account;
        couponUsageHistory.couponUsageStatus = CouponUsageStatus.ISSUED;
        return couponUsageHistory;
    }

    public void used(OrderItem orderItem) {
        this.orderItem = orderItem;
        couponUsageStatus = CouponUsageStatus.USED;
        usedAt = LocalDateTime.now();
    }

    public void restore() {
        this.orderItem =  null;
        couponUsageStatus = CouponUsageStatus.ISSUED;
    }
}
