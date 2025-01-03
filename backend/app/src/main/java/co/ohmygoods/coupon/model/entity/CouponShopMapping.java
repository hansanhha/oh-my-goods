package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.model.entity.Shop;
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

    public static CouponShopMapping toEntity(Coupon coupon, Shop shop) {
        CouponShopMapping couponShopMapping = new CouponShopMapping();
        couponShopMapping.coupon = coupon;
        couponShopMapping.applyTargetShop = shop;
        return couponShopMapping;
    }
}
