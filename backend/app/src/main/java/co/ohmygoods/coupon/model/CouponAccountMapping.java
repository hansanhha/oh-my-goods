package co.ohmygoods.coupon.model;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CouponAccountMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_target_account_id")
    private OAuth2Account issueTargetAccount;
}
