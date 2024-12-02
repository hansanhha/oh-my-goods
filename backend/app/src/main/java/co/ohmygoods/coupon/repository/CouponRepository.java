package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.Coupon;
import co.ohmygoods.shop.entity.Shop;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends CrudRepository<Coupon, Long> {

    @Query("SELECT c " +
            "FROM Coupon c " +
            "JOIN CouponShopMapping csm on csm.coupon = c " +
            "JOIN FETCH OAuth2Account a on c.issuer = a " +
            "WHERE csm.applyTargetShop = :shop")
    List<Coupon> fetchAllByShop(Shop shop);

    @Query("SELECT c " +
            "FROM Coupon c " +
            "JOIN CouponShopMapping  csm on csm.coupon = c " +
            "WHERE csm.applyTargetShop = :shop")
    Optional<Coupon> findByShopAndCouponId(Shop shop, Long couponId);
}
