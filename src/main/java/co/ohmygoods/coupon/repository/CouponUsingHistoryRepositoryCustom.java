package co.ohmygoods.coupon.repository;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsingHistory;
import co.ohmygoods.coupon.model.vo.CouponUsingStatus;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.model.entity.Shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


public interface CouponUsingHistoryRepositoryCustom {

    Optional<CouponUsingHistory> findByAccountAndCouponAndStatus(Account account, Coupon coupon, CouponUsingStatus status);

    /**
     * Coupon 엔티티를 fetch join 하여 CouponUsingHistory를 조회한다
     * <p>Account는 단순 필터링 조건으로만 사용된다</p>
     */
    Slice<CouponUsingHistory> fetchAllByAccountAndStatus(Account account, CouponUsingStatus status, Pageable pageable);

    /**
     *  Product 엔티티에 사용 가능한 플랫폼 쿠폰, 상점 쿠폰을 모두 조회한다
     *  메서드 내부에서 Product -> Shop을 불러온다
     */
    List<CouponUsingHistory> fetchAllUsableByProduct(Account account, Product product);

}
