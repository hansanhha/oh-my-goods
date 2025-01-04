package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.shop.model.entity.Shop;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends CrudRepository<Coupon, Long> {

    @Query("SELECT c " +
            "FROM Coupon c " +
            "JOIN CouponShopMapping csm " +
            "JOIN FETCH Account a  " +
            "WHERE csm.coupon = c " +
            "AND c.issuer = a AND csm.applyTargetShop = :shop")
    List<Coupon> fetchAllByShop(Shop shop, Pageable pageable);

    @Query("SELECT c " +
            "FROM Coupon c " +
            "WHERE c.id = :couponId " +
            "AND c IN " +
            "(SELECT csm.coupon FROM CouponShopMapping csm WHERE csm.applyTargetShop = :shop)")
    Optional<Coupon> findByShopAndCouponId(Shop shop, Long couponId);
}
