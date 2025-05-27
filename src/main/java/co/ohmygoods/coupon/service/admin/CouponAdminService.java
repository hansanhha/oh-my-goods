package co.ohmygoods.coupon.service.admin;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsableProduct;
import co.ohmygoods.coupon.model.entity.ShopCouponHistory;
import co.ohmygoods.coupon.model.vo.CouponDiscountType;
import co.ohmygoods.coupon.model.vo.CouponIssueTarget;
import co.ohmygoods.coupon.model.vo.CouponIssueQuantityLimitType;
import co.ohmygoods.coupon.repository.CouponUsableProductRepository;
import co.ohmygoods.coupon.repository.CouponRepository;
import co.ohmygoods.coupon.repository.ShopCouponHistoryRepository;
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
    private final CouponUsableProductRepository couponUsableProductRepository;
    private final ShopCouponHistoryRepository shopCouponHistoryRepository;
    private final ShopRepository shopRepository;

    /*
        필수 설정
        - 쿠폰 할인 타입: 정액 또는 정률
        - 할인 값 및 최대 할인 금액
        선택 설정
        - 쿠폰 적용 상품 대상: 전체 또는 일부 상품
        - 쿠폰 발급 가능 대상: 전체 또는 일부 유저
        - 쿠폰 발급 가능 제한: 제한 없음 또는 최대 발급 개수 제한
     */
    public ShopCouponResponse createShopCoupon(ShopCouponCreateRequest request) {
        Account shopAdmin = accountRepository.findByMemberId(request.shopAdminMemberId()).orElseThrow(AuthException::notFoundAccount);
        Shop shop = shopRepository.findByAdminMemberId(request.shopAdminMemberId()).orElseThrow(ShopException::notFoundShop);

        CouponIssueQuantityLimitType issueQuantityLimitType = CouponIssueQuantityLimitType
                .get(request.isLimitedMaxIssueCount(), request.isLimitedUsageCountPerAccount());

        CouponDiscountType discountType = request.isFixedDiscount() ? CouponDiscountType.FIXED : CouponDiscountType.PERCENTAGE;

        boolean isSpecificProductsIssuable = request.isSpecificProductsIssuable();

        Coupon coupon = Coupon.builder()
                .issuer(shopAdmin)
                .shop(shop)
                .name(request.couponName())
                .couponCode(request.couponCode())
                .issueQuantityLimitType(issueQuantityLimitType)
                .issuanceTarget(CouponIssueTarget.ALL_ACCOUNTS)
                .discountType(discountType)
                .discountValue(request.discountValue())
                .minimumPurchasePriceForApply(request.minimumPurchasePrice())
                .maxDiscountPrice(request.maxDiscountPrice())
                .maxIssuableQuantity(request.maxIssueCount())
                .maxUsageQuantityPerAccount(request.usageCountPerAccount())
                .validFrom(request.startDate())
                .validUntil(request.endDate())
                .buildShopCoupon(isSpecificProductsIssuable);

        Coupon createdCoupon = couponRepository.save(coupon);
        shopCouponHistoryRepository.save(ShopCouponHistory.toEntity(createdCoupon, shop));

        if (isSpecificProductsIssuable) {
            List<Product> targetProducts = productRepository.findAllByShopAndId(shop, request.issuableProductIds());
            List<CouponUsableProduct> couponUsableProducts = targetProducts
                    .stream()
                    .map(product -> CouponUsableProduct.toEntity(createdCoupon, product))
                    .toList();
            couponUsableProductRepository.saveAll(couponUsableProducts);
        }

        return ShopCouponResponse.from(createdCoupon, shop);
    }

    public Slice<ShopCouponResponse> getShopCouponCreateHistory(String adminMemberId, Pageable pageable) {
        Shop shop = shopRepository.findByAdminMemberId(adminMemberId).orElseThrow(ShopException::notFoundShop);
        Slice<Coupon> coupons = couponRepository.fetchAllByShop(shop, pageable);

        return coupons.map(coupon -> ShopCouponResponse.from(coupon, shop));
    }

}
