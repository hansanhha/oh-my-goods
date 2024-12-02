package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.CouponProductMapping;
import org.springframework.data.repository.CrudRepository;

public interface CouponProductMappingRepository extends CrudRepository<CouponProductMapping, Long> {
}
