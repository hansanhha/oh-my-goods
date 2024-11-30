package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.Coupon;
import org.springframework.data.repository.CrudRepository;

public interface CouponRepository extends CrudRepository<Coupon, Long> {
}
