package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsingHistory;
import co.ohmygoods.coupon.model.vo.CouponType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CouponUsingUsingHistoryRepository extends
CrudRepository<CouponUsingHistory, Long>, CouponUsingHistoryRepositoryCustom {

    List<CouponUsingHistory> findAllByCouponAndAccount(Coupon coupon, Account account);

    List<CouponUsingHistory> CouponType(CouponType couponType);

    @Query("SELECT cuh " +
            "FROM CouponUsingHistory cuh " +
            "WHERE cuh.id IN :couponHistoryIds")
    List<CouponUsingHistory> findAllByIds(List<Long> couponHistoryIds);

}
