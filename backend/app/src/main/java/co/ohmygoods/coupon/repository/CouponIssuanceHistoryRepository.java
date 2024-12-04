package co.ohmygoods.coupon.repository;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponIssuanceHistory;
import co.ohmygoods.product.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CouponIssuanceHistoryRepository extends CrudRepository<CouponIssuanceHistory, Long> {

    List<CouponIssuanceHistory> findAllByCouponAndAccount(Coupon coupon, OAuth2Account account);

    @Query("SELECT cih FROM CouponIssuanceHistory cih JOIN FETCH Coupon c WHERE cih.id = :id")
    Optional<CouponIssuanceHistory> fetchById(Long id);

    @Query("SELECT cih " +
            "FROM CouponIssuanceHistory  cih " +
            "JOIN FETCH Coupon c " +
            "WHERE cih.account = :account " +
            "AND cih.couponUsageStatus = 'ISSUED'")
    Slice<CouponIssuanceHistory> fetchIssuedStatusAllByAccount(OAuth2Account account, Pageable pageable);
}
