package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.ShopCouponHistory;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ShopCouponHistoryRepository extends CrudRepository<ShopCouponHistory, Long> {

    @Query("SELECT c " +
            "FROM Coupon c  " +
            "JOIN ShopCouponHistory csm ON csm.shop = :shop " +
            "WHERE csm.coupon IN :coupons")
    List<Coupon> findCouponsByCouponsAndShop(List<Coupon> coupons, Shop shop);
}
