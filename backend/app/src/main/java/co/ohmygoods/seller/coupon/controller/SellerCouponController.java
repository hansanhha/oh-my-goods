package co.ohmygoods.seller.coupon.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.seller.coupon.controller.dto.CreateShopCouponWebRequest;
import co.ohmygoods.seller.coupon.service.SellerCouponService;
import co.ohmygoods.seller.coupon.service.dto.CreateShopCouponRequest;
import co.ohmygoods.seller.coupon.service.dto.ShopCouponResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/seller/coupon")
@RestController
@RequiredArgsConstructor
public class SellerCouponController {

    private final SellerCouponService couponService;

    @GetMapping("/history")
    public ResponseEntity<?> getShopCouponHistory(@AuthenticationPrincipal AuthenticatedAccount account,
                                                  @RequestParam(required = false, defaultValue = "0") int page,
                                                  @RequestParam(required = false, defaultValue = "20") int size) {

        List<ShopCouponResponse> couponHistory = couponService.getShopCouponCreationHistory(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(couponHistory);
    }

    @PostMapping
    public ResponseEntity<?> createShopCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                                              @RequestBody CreateShopCouponWebRequest request) {

        CreateShopCouponRequest createShopCouponRequest = CreateShopCouponRequest.builder()
                .sellerMemberId(account.memberId())
                .isLimitedMaxIssueCount(request.isLimitedMaxIssueCount())
                .maxIssueCount(request.maxIssueCount())
                .isLimitedUsageCountPerAccount(request.isLimitedUsageCountPerAccount())
                .usageCountPerAccount(request.usageCountPerAccount())
                .isFixedDiscount(request.isFixedDiscount())
                .discountValue(request.discountValue())
                .minimumPurchasePrice(request.minimumPurchasePrice())
                .isApplicableSpecificProducts(request.isApplicableSpecificProducts())
                .applicableProductIds(request.applicableProductIds())
                .couponName(request.couponName())
                .couponCode(request.couponCode())
                .maxDiscountPrice(request.maxDiscountPrice())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        ShopCouponResponse created = couponService.createShopCoupon(createShopCouponRequest);
        return ResponseEntity.created(URI.create("")).body(Map.of("data", created));
    }

    @DeleteMapping("/{couponId}")
    public void deleteShopCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                                 @PathVariable Long couponId) {
        couponService.destroyIssuingShopCoupon(account.memberId(), couponId);
    }
}
