package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponProductMapping;
import co.ohmygoods.product.model.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponProductMappingRepository extends CrudRepository<CouponProductMapping, Long> {

    @Query("SELECT cpm " +
            "FROM CouponProductMapping cpm " +
            "JOIN FETCH Coupon c " +
            "WHERE cpm.coupon in :coupons AND " +
            "cpm.applyTargetProduct = :product")
    Slice<CouponProductMapping> fetchAllByCouponsAndProduct(List<Coupon> coupons, Product product, Pageable pageable);

    @Query("SELECT c " +
            "FROM Coupon c " +
            "JOIN CouponProductMapping cpm ON cpm.applyTargetProduct = :product " +
            "WHERE cpm.coupon IN :coupons")
    List<Coupon> findCouponsByCouponsAndProduct(List<Coupon> coupons, Product product);
}
