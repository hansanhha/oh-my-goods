package co.ohmygoods.seller.coupon.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.*;
import co.ohmygoods.coupon.repository.CouponAccountMappingRepository;
import co.ohmygoods.coupon.repository.CouponProductMappingRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.CouponShopMappingRepository;
import co.ohmygoods.seller.coupon.dto.IssueShopCouponResponse;
import co.ohmygoods.seller.coupon.dto.IssueShopCouponRequest;
import co.ohmygoods.shop.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerCouponService {

    private final AccountRepository accountRepository;
    private final CouponRepository couponRepository;
    private final CouponAccountMappingRepository couponAccountMappingRepository;
    private final CouponProductMappingRepository couponProductMappingRepository;
    private final CouponShopMappingRepository couponShopMappingRepository;
    private final ShopRepository shopRepository;

    /*
        유동 설정(쿠폰 유형)
        - 쿠폰 적용 상품 대상: shop 상품 전체 적용 또는 일부 적용
        - 쿠폰 발급 대상: 전체 유저 또는 일부 유저
        - 쿠폰 제한 개수 설정: 제한 없음 또는 최대 발급 개수 제한(최대 발급 개수 제한을 설정한 경우 사용자 별 쿠폰 사용 개수를 추적 필요)
        고정 설정(할인 유형)
        - 쿠폰 할인 타입 및 할인 값 설정: 정액, 정률 선택 및 할인 값
        - 최대 할인 금액 설정
     */
    public IssueShopCouponResponse issueCoupon(IssueShopCouponRequest request) {
        OAuth2Account issuer = accountRepository.findByEmail(request.issuerEmail()).orElseThrow(CouponException::notFoundIssuer);
        Shop shop = shopRepository.findById(request.shopId()).orElseThrow(CouponException::notFoundShop);

        CouponLimitConditionType limitConditionType = convertToCouponLimitConditionType(request.isLimitedMaxIssueCount(), request.isLimitedUsageCountPerAccount());

        CouponDiscountType discountType = request.isFixedDiscount()
                ? CouponDiscountType.FIXED : CouponDiscountType.PERCENTAGE;

        Coupon coupon = Coupon.builder()
                .issuer(issuer)
                .name(request.couponName())
                .couponCode(request.couponCode())
                .limitedConditionType(limitConditionType)
                .issuanceTarget(CouponIssuanceTarget.ALL_ACCOUNTS)
                .discountType(discountType)
                .discountValue(request.discountValue())
                .maxDiscountPrice(request.maxDiscountPrice())
                .maxIssuableCount(request.maxIssueCount())
                .maxUsageCountPerAccount(request.usageCountPerAccount())
                .validFrom(request.startDate())
                .validUntil(request.endDate())
                .buildShopCoupon(request.isApplicableSpecificProducts());

        Coupon savedCoupon = couponRepository.save(coupon);

        CouponShopMapping couponShopMapping = CouponShopMapping.toEntity(savedCoupon, shop);
        CouponShopMapping savedCouponShopMapping = couponShopMappingRepository.save(couponShopMapping);

        return IssueShopCouponResponse.from(savedCoupon, savedCouponShopMapping);
    }

    private CouponLimitConditionType convertToCouponLimitConditionType(boolean limitedMaxIssueCount, boolean limitedUsageCountPerAccount) {
        if (limitedMaxIssueCount && limitedUsageCountPerAccount) {
            return CouponLimitConditionType.FULL_LIMITED;
        } else if (limitedMaxIssueCount) {
            return CouponLimitConditionType.MAX_ISSUABLE_LIMITED;
        } else if (limitedUsageCountPerAccount) {
            return CouponLimitConditionType.PER_ACCOUNT_LIMITED;
        } else {
            return CouponLimitConditionType.UNLIMITED;
        }
    }

}