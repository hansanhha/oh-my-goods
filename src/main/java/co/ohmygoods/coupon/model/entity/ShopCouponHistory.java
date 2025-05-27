package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.model.entity.Shop;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ShopCouponHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_target_shop_id")
    private Shop shop;

    public static ShopCouponHistory toEntity(Coupon coupon, Shop shop) {
        ShopCouponHistory shopCouponHistory = new ShopCouponHistory();
        shopCouponHistory.coupon = coupon;
        shopCouponHistory.shop = shop;
        return shopCouponHistory;
    }
}
