package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponIssuanceHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponIssuanceHistoryRepository extends CrudRepository<CouponIssuanceHistory, Long> {

    List<CouponIssuanceHistory> findAllByCouponAndAccount(Coupon coupon, OAuth2Account account);
}
