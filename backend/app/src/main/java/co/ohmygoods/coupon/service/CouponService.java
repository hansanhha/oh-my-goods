package co.ohmygoods.coupon.service;

import co.ohmygoods.coupon.repository.CouponAccountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponAccountHistoryRepository couponUsageHistoryRepository;

    /*
    todo coupon
     - 쿠폰 발급 api: 상품 상세 페이지 또는 쿠폰 발급 페이지
     - 쿠폰 사용 api: 결제 진행 시 쿠폰 적용 금액 계산 및 쿠폰 사용 처리
     - 쿠폰 보유 내역 api
     */

    public void
}
