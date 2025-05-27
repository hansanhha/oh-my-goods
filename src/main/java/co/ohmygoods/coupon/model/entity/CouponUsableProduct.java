package co.ohmygoods.coupon.model.entity;


import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.model.entity.Product;

import jakarta.persistence.*;

import lombok.Getter;


@Entity
@Getter
public class CouponUsableProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_target_product_id")
    private Product product;

    public static CouponUsableProduct toEntity(Coupon coupon, Product applyTargetProduct) {
        CouponUsableProduct couponUsableProduct = new CouponUsableProduct();
        couponUsableProduct.coupon = coupon;
        couponUsableProduct.product = applyTargetProduct;
        return couponUsableProduct;
    }
}
