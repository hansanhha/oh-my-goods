package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.model.vo.CouponUsageStatus;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class CouponIssuanceHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponUsageStatus couponUsageStatus;

    private LocalDateTime usedAt;
}
