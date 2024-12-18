package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponShopMapping;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponShopMappingRepository extends CrudRepository<CouponShopMapping, Long> {

    @Query("SELECT c " +
            "FROM Coupon c  " +
            "JOIN CouponShopMapping csm ON csm.applyTargetShop = :shop " +
            "WHERE csm.coupon IN :coupons")
    List<Coupon> findCouponsByCouponsAndShop(List<Coupon> coupons, Shop shop);
}
