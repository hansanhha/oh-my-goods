package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CouponUsageHistoryRepository extends CrudRepository<CouponUsageHistory, Long> {

    List<CouponUsageHistory> findAllByCouponAndAccount(Coupon coupon, OAuth2Account account);

    @Query("SELECT cuh " +
            "FROM CouponUsageHistory cuh " +
            "JOIN FETCH cuh.account a ON a = :account " +
            "JOIN FETCH cuh.coupon c ON c = :coupon " +
            "WHERE cuh.couponUsageStatus = 'ISSUED' ")
    Optional<CouponUsageHistory> fetchFirstByAccountAndCouponAndCouponUsageStatusIssued(OAuth2Account account, Coupon coupon);

    @Query("SELECT cuh " +
            "FROM CouponUsageHistory  cuh " +
            "JOIN FETCH Coupon c " +
            "WHERE cuh.account = :account " +
            "AND cuh.couponUsageStatus = 'ISSUED'")
    Slice<CouponUsageHistory> fetchIssuedStatusAllByAccount(OAuth2Account account, Pageable pageable);

    @Query("SELECT cuh " +
            "FROM CouponUsageHistory cuh " +
            "JOIN cuh.account ON cuh.account.email = :accountEmail " +
            "WHERE cuh.id IN :couponUsageHistoryIds")
    List<CouponUsageHistory> findAllByAccountEmailAndId(String accountEmail, List<Long> couponUsageHistoryIds);
}
