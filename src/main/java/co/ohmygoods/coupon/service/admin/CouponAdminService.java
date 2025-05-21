package co.ohmygoods.coupon.service.admin;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponProductMapping;
import co.ohmygoods.coupon.model.entity.CouponShopMapping;
import co.ohmygoods.coupon.model.vo.CouponDiscountType;
import co.ohmygoods.coupon.model.vo.CouponIssuanceTarget;
import co.ohmygoods.coupon.model.vo.CouponIssueQuantityLimitType;
import co.ohmygoods.coupon.repository.CouponProductMappingRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.CouponShopMappingRepository;
import co.ohmygoods.coupon.repository.CouponHistoryRepository;
import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.product.repository.ProductRepository;
import co.ohmygoods.coupon.service.admin.dto.ShopCouponCreateRequest;
import co.ohmygoods.coupon.service.admin.dto.ShopCouponResponse;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.repository.ShopRepository;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class CouponAdminService {

    private final AccountRepository accountRepository;
    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final CouponHistoryRepository couponHistoryRepository;
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
    public ShopCouponResponse createShopCoupon(ShopCouponCreateRequest request) {
        Account issuer = accountRepository.findByMemberId(request.sellerMemberId()).orElseThrow(AuthException::notFoundAccount);
        Shop shop = shopRepository.findByAdminMemberId(request.sellerMemberId()).orElseThrow(ShopException::notFoundShop);

        CouponIssueQuantityLimitType issueQuantityLimitType = CouponIssueQuantityLimitType.get(
                request.isLimitedMaxIssueCount(), request.isLimitedUsageCountPerAccount());

        CouponDiscountType discountType = request.isFixedDiscount()
                ? CouponDiscountType.FIXED : CouponDiscountType.PERCENTAGE;

        boolean applicableSpecificProducts = request.isApplicableSpecificProducts();

        Coupon coupon = Coupon.builder()
                .issuer(issuer)
                .name(request.couponName())
                .couponCode(request.couponCode())
                .issueQuantityLimitType(issueQuantityLimitType)
                .issuanceTarget(CouponIssuanceTarget.ALL_ACCOUNTS)
                .discountType(discountType)
                .discountValue(request.discountValue())
                .minimumPurchasePriceForApply(request.minimumPurchasePrice())
                .maxDiscountPrice(request.maxDiscountPrice())
                .maxIssuableQuantity(request.maxIssueCount())
                .maxUsageQuantityPerAccount(request.usageCountPerAccount())
                .validFrom(request.startDate())
                .validUntil(request.endDate())
                .buildShopCoupon(applicableSpecificProducts);

        Coupon savedCoupon = couponRepository.save(coupon);

        CouponShopMapping couponShopMapping = CouponShopMapping.toEntity(savedCoupon, shop);
        CouponShopMapping savedCouponShopMapping = couponShopMappingRepository.save(couponShopMapping);

        if (applicableSpecificProducts) {
            List<Product> shopCouponApplicableProducts = productRepository.findAllByShopAndId(shop, request.applicableProductIds());
            List<CouponProductMapping> couponProductMappings = shopCouponApplicableProducts
                    .stream()
                    .map(product -> CouponProductMapping.toEntity(savedCoupon, product))
                    .toList();
            couponProductMappingRepository.saveAll(couponProductMappings);
        }

        return ShopCouponResponse.from(savedCoupon, shop);
    }

    public Slice<ShopCouponResponse> getShopCouponCreationHistory(String sellerMemberId, Pageable pageable) {
        Shop shop = shopRepository.findByAdminMemberId(sellerMemberId).orElseThrow(ShopException::notFoundShop);
        Slice<Coupon> coupons = couponRepository.fetchAllByShop(shop, pageable);

        return coupons.map(coupon -> ShopCouponResponse.from(coupon, shop));
    }

    public void destroyIssuingShopCoupon(String sellerMemberId, Long couponId) {
        Shop shop = shopRepository.findByAdminMemberId(sellerMemberId).orElseThrow(ShopException::notFoundShop);
        Coupon coupon = couponRepository.findByShopAndCouponId(shop, couponId).orElseThrow(CouponException::notFoundCoupon);

        coupon.destroy();
    }

}
