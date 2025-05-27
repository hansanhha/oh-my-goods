package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.vo.CouponUsingStatus;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.model.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CouponUsingHistory extends BaseEntity {

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
    private CouponUsingStatus couponUsingStatus;

    private LocalDateTime usedAt;

    public static CouponUsingHistory issued(Coupon coupon, Account account) {
        CouponUsingHistory cuh = new CouponUsingHistory();
        cuh.coupon = coupon;
        cuh.account = account;
        cuh.couponUsingStatus = CouponUsingStatus.ISSUED;
        return cuh;
    }

    public void used(OrderItem orderItem) {
        this.orderItem = orderItem;
        couponUsingStatus = CouponUsingStatus.USED;
        usedAt = LocalDateTime.now();
    }

    public void restore() {
        this.orderItem =  null;
        couponUsingStatus = CouponUsingStatus.ISSUED;
    }

    public boolean isUsed() {
        return couponUsingStatus.equals(CouponUsingStatus.USED);
    }
}
