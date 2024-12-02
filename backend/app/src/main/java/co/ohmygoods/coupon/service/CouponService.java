package co.ohmygoods.coupon.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponIssuanceHistory;
import co.ohmygoods.coupon.repository.CouponIssuanceHistoryRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponValidationService couponValidationService;
    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;
    private final CouponIssuanceHistoryRepository couponIssuanceHistoryRepository;

    /*
    todo coupon
     - 쿠폰 발급 api: 상품 상세 페이지 또는 쿠폰 발급 페이지
     - 쿠폰 사용 api: 결제 진행 시 쿠폰 적용 금액 계산 및 쿠폰 사용 처리
     - 쿠폰 보유 내역 api
     */

    public void issueCoupon(Long couponId, String accountEmail) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);
        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(CouponException::notFoundAccount);
        List<CouponIssuanceHistory> couponAccountHistories = couponIssuanceHistoryRepository.findAllByCouponAndAccount(coupon, account);

        couponValidationService.validateBeforeIssue(coupon, account, couponAccountHistories.size());
    }
}
