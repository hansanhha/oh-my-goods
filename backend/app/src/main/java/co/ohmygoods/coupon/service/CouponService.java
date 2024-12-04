package co.ohmygoods.coupon.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.dto.ApplicableIssuedCouponResponse;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponIssuanceHistory;
import co.ohmygoods.coupon.model.vo.CouponApplicableProductScope;
import co.ohmygoods.coupon.repository.CouponIssuanceHistoryRepository;
import co.ohmygoods.coupon.repository.CouponProductMappingRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.CouponShopMappingRepository;
import co.ohmygoods.product.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.shop.entity.Shop;
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

    private final CouponValidationService couponValidationService;
    private final CouponRepository couponRepository;
    private final AccountRepository accountRepository;
    private final CouponIssuanceHistoryRepository couponIssuanceHistoryRepository;
    private final CouponProductMappingRepository couponProductMappingRepository;
    private final ProductRepository productRepository;
    private final CouponShopMappingRepository couponShopMappingRepository;

    /*
    todo coupon
     - 쿠폰 발급 api: 상품 상세 페이지 또는 쿠폰 발급 페이지
     - 쿠폰 사용 api: 결제 진행 시 쿠폰 적용 금액 계산 및 쿠폰 사용 처리
     - 발급된 사용 가능한 쿠폰 목록 조회 api
     - 특정 상품에 적용할 수 있는 쿠폰 조회 api
     */

    public void issueCoupon(Long couponId, String accountEmail) {
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(CouponException::notFoundCoupon);
        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(CouponException::notFoundAccount);
        List<CouponIssuanceHistory> couponAccountHistories = couponIssuanceHistoryRepository.findAllByCouponAndAccount(coupon, account);

        couponValidationService.validateBeforeIssue(coupon, account, couponAccountHistories.size());

        coupon.issue();
        CouponIssuanceHistory couponIssuanceHistory = CouponIssuanceHistory.issued(coupon, account);

        couponIssuanceHistoryRepository.save(couponIssuanceHistory);
    }

    public int applyCoupon(Long couponIssuanceHistoryId, int targetProductOriginalPrice) {
        CouponIssuanceHistory couponIssuanceHistory = couponIssuanceHistoryRepository.fetchById(couponIssuanceHistoryId)
                .orElseThrow(CouponException::notFoundCouponIssuanceHistory);

        couponValidationService.validateBeforeUse(couponIssuanceHistory);

        Coupon coupon = couponIssuanceHistory.getCoupon();

        int discountedPrice = coupon.calculate(targetProductOriginalPrice);
        couponIssuanceHistory.used();

        return discountedPrice;
    }

    public Slice<ApplicableIssuedCouponResponse> getAllApplicableIssuedCoupons(String accountEmail, Pageable pageableNullable) {
        Pageable pageable = getNullProcessingPageable(pageableNullable);

        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(CouponException::notFoundAccount);
        Slice<CouponIssuanceHistory> couponIssuanceHistories = couponIssuanceHistoryRepository.fetchIssuedStatusAllByAccount(account, pageable);

        List<ApplicableIssuedCouponResponse> issuedCouponResponses = couponIssuanceHistories
                .stream()
                .map(history -> ApplicableIssuedCouponResponse.from(history.getCoupon(), null))
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
     * 대상 상품에 적용할 수 있는 쿠폰을 쿠폰 종류와 적용 대상 여부를 기반으로 필터링함
     */
    public Slice<ApplicableIssuedCouponResponse> getIssuedCouponsApplicableToProduct(String accountEmail, Long productId, Pageable pageableNullable) {
        Pageable pageable = getNullProcessingPageable(pageableNullable);

        OAuth2Account account = accountRepository.findByEmail(accountEmail).orElseThrow(CouponException::notFoundAccount);
        Product targetProduct = productRepository.findById(productId).orElseThrow(CouponException::notFoundProduct);

        final int targetProductOriginalPrice = targetProduct.getOriginalPrice();
        Shop targetProductShop = targetProduct.getShop();

        // 사용자가 대상 상품에 적용할 수 있는 모든 종류의 쿠폰 목록
        HashSet<Coupon> applicableCoupons = new HashSet<>();

        // 사용자에게 발급된 모든 종류의 사용 가능한 상태의 쿠폰 목록
        Slice<CouponIssuanceHistory> couponIssuanceHistories = couponIssuanceHistoryRepository.fetchIssuedStatusAllByAccount(account, pageable);
        Map<CouponApplicableProductScope, List<Coupon>> issuedAllCoupons =
                couponIssuanceHistories
                .stream()
                .map(CouponIssuanceHistory::getCoupon)
                .collect(Collectors.groupingBy(Coupon::getApplicableProductScope));

        // "애플리케이션 전체 상품 적용 쿠폰"을 적용 가능 쿠폰 목록에 삽입
        applicableCoupons.addAll(issuedAllCoupons.get(CouponApplicableProductScope.ALL_PRODUCTS));

        // 대상 상품에 적용 가능한 쿠폰 필터링 작업
        // 1. "애플리케이션 일부 상품 적용 쿠폰" 중 대상 상품이 포함된 쿠폰 필터링 및 적용 가능 쿠폰 목록 삽입
        List<Coupon> specificProductGeneralCoupons = issuedAllCoupons.get(CouponApplicableProductScope.SPECIFIC_PRODUCTS);
        if (!specificProductGeneralCoupons.isEmpty()) {
            List<Coupon> specificProductApplicableGeneralCoupons =
                    couponProductMappingRepository.findCouponsByCouponsAndProduct(specificProductGeneralCoupons, targetProduct);
            applicableCoupons.addAll(specificProductApplicableGeneralCoupons);
        }

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

            // 4. "상점 일부 상품 적용 쿠폰" 중 대상 상품이 포함된 쿠폰 필터링 및 적용 가능 쿠폰 목록 삽입
            List<Coupon> specificShopProductShopCoupons = targetShopCoupons.get(CouponApplicableProductScope.SPECIFIC_PRODUCTS);
            if (!specificShopProductShopCoupons.isEmpty()) {
                List<Coupon> specificShopProductApplicableShopCoupons =
                        couponProductMappingRepository.findCouponsByCouponsAndProduct(specificShopProductShopCoupons, targetProduct);
                applicableCoupons.addAll(specificShopProductApplicableShopCoupons);
            }
        }

        List<ApplicableIssuedCouponResponse> applicableIssuedCouponResponses = applicableCoupons.stream()
                .map(coupon -> ApplicableIssuedCouponResponse.from(coupon, targetProductOriginalPrice))
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
