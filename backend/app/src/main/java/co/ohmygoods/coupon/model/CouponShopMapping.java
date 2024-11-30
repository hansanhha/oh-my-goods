package co.ohmygoods.coupon.model;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CouponShopMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_target_shop_id")
    private Shop applyTargetShop;
}
