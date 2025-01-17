package co.ohmygoods.coupon.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponHistory;
import co.ohmygoods.coupon.model.vo.CouponApplicableProductScope;
import co.ohmygoods.coupon.repository.CouponProductMappingRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.CouponShopMappingRepository;
import co.ohmygoods.coupon.repository.CouponHistoryRepository;
import co.ohmygoods.coupon.service.dto.ApplicableIssuedCouponResponse;
import co.ohmygoods.global.lock.DistributedLock;
import co.ohmygoods.order.exception.OrderException;
import co.ohmygoods.order.model.entity.OrderItem;
import co.ohmygoods.order.repository.OrderItemRepository;
import co.ohmygoods.product.exception.ProductException;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.model.entity.Shop;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;
    private final CouponHistoryRepository couponHistoryRepository;
    private final CouponProductMappingRepository couponProductMappingRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final CouponShopMappingRepository couponShopMappingRepository;

    // 쿠폰 발급 및 발급 이력 저장
    @DistributedLock(key = "coupon:issue:#couponId")
    public void issueCoupon(String memberId, Long couponId) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        List<CouponHistory> couponAccountHistories = couponHistoryRepository.findAllByCouponAndAccount(coupon, account);

        CouponValidationService.validateBeforeIssue(coupon, account, couponAccountHistories.size());

        coupon.issue();
        CouponHistory couponHistory = CouponHistory.issued(coupon, account);

        couponHistoryRepository.save(couponHistory);
    }

    // 쿠폰 적용 및 최대 할인 금액 계산
    public int applyCoupon(String accountEmail, Long orderItemId, Long couponId, int targetProductPrice) {
        Account account = accountRepository.findByEmail(accountEmail).orElseThrow(AuthException::notFoundAccount);
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(OrderException::notFoundOrderItem);
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);

        CouponHistory couponHistory = couponHistoryRepository.fetchIssuedCouponHistoryByAccountAndCoupon(account, coupon)
                .orElseThrow(CouponException::notFoundCouponIssuanceHistory);

        CouponValidationService.validateBeforeUse(couponHistory.getCouponHistoryStatus(),
                coupon.getMinimumPurchasePriceForApply(), targetProductPrice);

        int discountedPrice = coupon.calculate(targetProductPrice);
        couponHistory.used(orderItem);

        return discountedPrice;
    }

    public void restoreAppliedCoupon(String accountEmail, List<Long> couponHistoryIds) {
        List<CouponHistory> couponUsageHistories = couponHistoryRepository.findAllByAccountEmailAndId(accountEmail, couponHistoryIds);

        couponUsageHistories.forEach(CouponHistory::restore);
    }

    /**
     *
     * @param accountEmail 쿠폰 발급 조회 계정 이메일
     * @param pageableNullable 쿠폰 목록 조회용 pageable (nullable)
     * @return 발급된 쿠폰 목록
     *
     * 사용자에게 발급된 쿠폰 목록
     */
    public Slice<ApplicableIssuedCouponResponse> getAllApplicableIssuedCoupons(String accountEmail, Pageable pageableNullable) {
        Pageable pageable = getNullProcessingPageable(pageableNullable);

        Account account = accountRepository.findByEmail(accountEmail).orElseThrow(AuthException::notFoundAccount);
        Slice<CouponHistory> couponIssuanceHistories = couponHistoryRepository.fetchAllIssuedCouponHistoryByAccount(account, pageable);

        List<ApplicableIssuedCouponResponse> issuedCouponResponses = couponIssuanceHistories
                .stream()
                .map(CouponHistory::getCoupon)
                .map(ApplicableIssuedCouponResponse::from)
                .toList();

        return new SliceImpl<>(issuedCouponResponses, pageable, couponIssuanceHistories.hasNext());
    }

    /**
     * @param accountEmail     쿠폰 발급 조회 계정 이메일
     * @param productId        적용 가능한 쿠폰을 조회할 상품 id
     * @param pageableNullable 쿠폰 목록 조회용 pageable (nullable)
     * @return 대상 상품에 적용 가능한 쿠폰 목록
     *
     * 특정 상품에 적용 가능한 쿠폰 종류
     * - 애플리케이션 전체 상품 적용 쿠폰(애플리케이션 운영자 발급)
     * - 애플리케이션 일부 상품 적용 쿠폰(애플리케이션 운영자 발급)
     * - 상점 전체 상품 적용 쿠폰(상점 판매자 발급)
     * - 상점 일부 상품 적용 쿠폰(상점 판매자 발급)
     *
     * 쿠폰 종류와 적용 대상 여부를 기반으로 대상 상품에 적용할 수 있는 쿠폰을 필터링
     */
    public Slice<ApplicableIssuedCouponResponse> getIssuedCouponsApplicableToProduct(String accountEmail, Long productId, Pageable pageableNullable) {
        Pageable pageable = getNullProcessingPageable(pageableNullable);

        Account account = accountRepository.findByEmail(accountEmail).orElseThrow(AuthException::notFoundAccount);
        Product targetProduct = productRepository.findById(productId).orElseThrow(ProductException::notFoundProduct);

        final int targetProductOriginalPrice = targetProduct.getOriginalPrice();
        Shop targetProductShop = targetProduct.getShop();

        // 사용자가 대상 상품에 적용할 수 있는 모든 종류의 쿠폰 목록
        HashSet<Coupon> applicableCoupons = new HashSet<>();

        // 사용자에게 발급된 모든 종류의 사용 가능한 상태의 쿠폰 목록
        // 쿠폰 적용 범위로 그룹화
        Slice<CouponHistory> couponIssuanceHistories = couponHistoryRepository.fetchAllIssuedCouponHistoryByAccount(account, pageable);
        Map<CouponApplicableProductScope, List<Coupon>> issuedAllCoupons =
                couponIssuanceHistories
                .stream()
                .map(CouponHistory::getCoupon)
                .collect(Collectors.groupingBy(Coupon::getApplicableProductScope));

        // "애플리케이션 전체 상품 적용 쿠폰"을 적용 가능한 쿠폰 목록에 삽입
        applicableCoupons.addAll(issuedAllCoupons.get(CouponApplicableProductScope.ALL_PRODUCTS));

        /* ------------------------------------
            대상 상품에 적용 가능한 쿠폰 필터링 작업
           ------------------------------------ */

        // 1. "애플리케이션 일부 상품 적용 쿠폰" 중 대상 상품이 포함된 쿠폰 필터링 후 적용 가능 쿠폰 목록에 삽입
        List<Coupon> specificProductGeneralCoupons = issuedAllCoupons.get(CouponApplicableProductScope.SPECIFIC_PRODUCTS);
        if (!specificProductGeneralCoupons.isEmpty()) {
            List<Coupon> specificProductApplicableGeneralCoupons =
                    couponProductMappingRepository.findCouponsByCouponsAndProduct(specificProductGeneralCoupons, targetProduct);
            applicableCoupons.addAll(specificProductApplicableGeneralCoupons);
        }

        // 모든 상점에서 발급한 쿠폰 목록
        List<Coupon> allShopCoupons = Stream.concat(
                        issuedAllCoupons.get(CouponApplicableProductScope.SHOP_ALL_PRODUCTS).stream(),
                        issuedAllCoupons.get(CouponApplicableProductScope.SHOP_SPECIFIC_PRODUCTS).stream())
                .toList();

        // 2. 대상 상품에 적용 가능한 상점 쿠폰 조회 및 쿠폰 적용 범위에 따라 그룹화
        if (!allShopCoupons.isEmpty()) {
            Map<CouponApplicableProductScope, List<Coupon>> targetShopCoupons = couponShopMappingRepository
                    .findCouponsByCouponsAndShop(allShopCoupons, targetProductShop)
                    .stream()
                    .collect(Collectors.groupingBy(Coupon::getApplicableProductScope));

            // 3. "상점 전체 상품 적용 쿠폰"을 적용 가능 쿠폰 목록에 삽입
            applicableCoupons.addAll(targetShopCoupons.get(CouponApplicableProductScope.SHOP_ALL_PRODUCTS));

            // 4. "상점 일부 상품 적용 쿠폰" 중 대상 상품이 포함된 쿠폰 필터링 후 적용 가능 쿠폰 목록에 삽입
            List<Coupon> specificShopProductShopCoupons = targetShopCoupons.get(CouponApplicableProductScope.SPECIFIC_PRODUCTS);
            if (!specificShopProductShopCoupons.isEmpty()) {
                List<Coupon> specificShopProductApplicableShopCoupons =
                        couponProductMappingRepository.findCouponsByCouponsAndProduct(specificShopProductShopCoupons, targetProduct);
                applicableCoupons.addAll(specificShopProductApplicableShopCoupons);
            }
        }

        List<ApplicableIssuedCouponResponse> applicableIssuedCouponResponses = applicableCoupons.stream()
                .map(ApplicableIssuedCouponResponse::from)
                .toList();

        return new SliceImpl<>(applicableIssuedCouponResponses, pageable, couponIssuanceHistories.hasNext());
    }

    private Pageable getNullProcessingPageable(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.ofSize(20);
        }

        return pageable;
    }
}
