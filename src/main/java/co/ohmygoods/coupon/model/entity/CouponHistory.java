package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.vo.CouponHistoryStatus;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.model.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CouponHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponHistoryStatus couponHistoryStatus;

    private LocalDateTime usedAt;

    public static CouponHistory issued(Coupon coupon, Account account) {
        CouponHistory couponHistory = new CouponHistory();
        couponHistory.coupon = coupon;
        couponHistory.account = account;
        couponHistory.couponHistoryStatus = CouponHistoryStatus.ISSUED;
        return couponHistory;
    }

    public void used(OrderItem orderItem) {
        this.orderItem = orderItem;
        couponHistoryStatus = CouponHistoryStatus.USED;
        usedAt = LocalDateTime.now();
    }

    public void restore() {
        this.orderItem =  null;
        couponHistoryStatus = CouponHistoryStatus.ISSUED;
    }
}
