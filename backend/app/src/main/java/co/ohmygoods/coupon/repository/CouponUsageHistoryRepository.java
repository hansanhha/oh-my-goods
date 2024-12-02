package co.ohmygoods.coupon.repository;

import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
import org.springframework.data.repository.CrudRepository;

public interface CouponUsageHistoryRepository extends CrudRepository<CouponUsageHistory, Long> {
}
