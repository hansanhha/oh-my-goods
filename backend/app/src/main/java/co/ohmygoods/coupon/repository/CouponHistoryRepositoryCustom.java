package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface CouponHistoryRepositoryCustom {

    Optional<CouponHistory> fetchIssuedCouponHistoryByAccountAndCoupon(Account account, Coupon coupon);

    Slice<CouponHistory> fetchAllIssuedCouponHistoryByAccount(Account account, Pageable pageable);

    List<CouponHistory> findAllByAccountEmailAndId(String accountEmail, List<Long> couponHistoryIds);
}
