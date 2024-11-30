package co.ohmygoods.coupon.model;

import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class CouponProductMapping extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apply_target_product_id")
    private Product applyTargetProduct;
}
