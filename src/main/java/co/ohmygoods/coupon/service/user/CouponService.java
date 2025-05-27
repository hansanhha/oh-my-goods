package co.ohmygoods.coupon.service.user;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsingHistory;
import co.ohmygoods.coupon.model.service.CouponValidationService;
import co.ohmygoods.coupon.model.vo.CouponDiscountCalculator;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.CouponUsingUsingHistoryRepository;
import co.ohmygoods.coupon.service.user.dto.CouponResponse;
import co.ohmygoods.global.lock.DistributedLock;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static co.ohmygoods.coupon.model.vo.CouponUsingStatus.*;


@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;
    private final CouponUsingUsingHistoryRepository couponUsingHistoryRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    // 쿠폰 발급 및 발급 이력 저장
    @DistributedLock(key = "coupon:issue:#couponId")
    public void issue(String memberId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        List<CouponUsingHistory> couponAccountHistories = couponUsingHistoryRepository.findAllByCouponAndAccount(coupon, account);

        if (!coupon.requireIssueValidation()) {
            CouponValidationService.validateIssuable(coupon, account, couponAccountHistories.size());
        }

        coupon.issue();
        CouponUsingHistory couponUsingHistory = CouponUsingHistory.issued(coupon, account);

        couponUsingHistoryRepository.save(couponUsingHistory);
    }

    // 쿠폰 적용 및 최대 할인 금액 계산
    public int use(String memberId, Long orderItemId, Long couponId, int targetProductPrice) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(OrderException::notFoundOrderItem);
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);

        CouponUsingHistory couponUsingHistory = couponUsingHistoryRepository
                .findByAccountAndCouponAndStatus(account, coupon, ISSUED)
                .orElseThrow(CouponException::notFoundCouponIssuanceHistory);

        if (couponUsingHistory.isUsed()) {
            throw CouponException.COUPON_ALREADY_USED;
        }

        int discountedPrice = CouponDiscountCalculator
                .calculate(coupon.getDiscountType(), coupon.getDiscountValue(), coupon.getMaxDiscountPrice(), targetProductPrice);
        couponUsingHistory.used(orderItem);

        return discountedPrice;
    }

    public void restoreUsedCoupon(List<Long> couponHistoryIds) {
        List<CouponUsingHistory> couponUsageHistories = couponUsingHistoryRepository.findAllByIds(couponHistoryIds);

        couponUsageHistories.forEach(CouponUsingHistory::restore);
    }

    /**
     *
     * @param memberId 쿠폰 발급 조회 계정 식별자
     * @param pageable 쿠폰 목록 조회용 pageable
     * @return 발급된 쿠폰 목록
     *
     * 사용자에게 발급된 쿠폰 목록
     */
    public Slice<CouponResponse> getUsableCoupons(String memberId, Pageable pageable) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        Slice<CouponUsingHistory> couponUsingHistories = couponUsingHistoryRepository.fetchAllByAccountAndStatus(account, ISSUED, pageable);

        List<CouponResponse> usableCoupons = couponUsingHistories
                .stream()
                .map(CouponUsingHistory::getCoupon)
                .map(CouponResponse::from)
                .toList();

        return new SliceImpl<>(usableCoupons, pageable, couponUsingHistories.hasNext());
    }

    /**
     * <ul>특정 상품에 적용 가능한 쿠폰 종류</ul>
     * <ol>전체 상품 적용 플랫폼 쿠폰</ol>
     * <ol>일부 상품 적용 플랫폼 쿠폰</ol>
     * <ol>전체 상품 적용 상점 쿠폰</ol>
     * <ol>일부 상품 적용 상점 쿠폰</ol>
     *
     * @param memberId     쿠폰 발급 조회 계정 식별자
     * @param productId    적용 가능한 쿠폰을 조회할 상품 id
     * @return 대상 상품에 적용 가능한 쿠폰 목록
     *
     * 쿠폰 종류와 적용 대상 여부를 기반으로 대상 상품에 적용할 수 있는 쿠폰을 필터링
     */
    public List<CouponResponse> getUsableCouponsOnTargetProduct(String memberId, Long productId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        Product targetProduct = productRepository.fetchShopById(productId).orElseThrow(ProductException::notFoundProduct);

        // 사용자가 발급한 플랫폼 쿠폰과 상점 쿠폰 목록
        List<CouponUsingHistory> couponUsingHistories = couponUsingHistoryRepository.fetchAllUsableByProduct(account, targetProduct);

        return couponUsingHistories
                .stream()
                .map(CouponUsingHistory::getCoupon)
                .map(CouponResponse::from)
                .toList();
    }

}
