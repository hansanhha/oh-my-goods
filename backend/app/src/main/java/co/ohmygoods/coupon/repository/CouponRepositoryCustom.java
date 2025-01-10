package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface CouponRepositoryCustom {

    Slice<Coupon> fetchAllByShop(Shop shop, Pageable pageable);

    Optional<Coupon> findByShopAndCouponId(Shop shop, Long couponId);
}
