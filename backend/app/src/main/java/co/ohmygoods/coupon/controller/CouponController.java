package co.ohmygoods.coupon.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.coupon.service.CouponService;
import co.ohmygoods.coupon.service.dto.ApplicableIssuedCouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/coupons")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<?> getCoupons(@AuthenticationPrincipal AuthenticatedAccount account,
                                        @RequestParam(required = false, defaultValue = "-1") Long productId,
                                        @RequestParam(required = false, defaultValue = "0") int page,
                                        @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ApplicableIssuedCouponResponse> coupons;

        if (productId > 0) {
             coupons = couponService.getAllApplicableIssuedCoupons(account.memberId(), Pageable.ofSize(size).withPage(page));
        } else {
            coupons = couponService.getIssuedCouponsApplicableToProduct(account.memberId(), productId, Pageable.ofSize(size).withPage(page));
        }

        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/{couponId}/issue")
    public void issueCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                            @PathVariable Long couponId) {
        couponService.issueCoupon(account.memberId(), couponId);
    }
}
