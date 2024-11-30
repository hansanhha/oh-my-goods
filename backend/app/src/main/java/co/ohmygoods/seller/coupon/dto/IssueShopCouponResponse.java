package co.ohmygoods.seller.coupon.dto;

import co.ohmygoods.coupon.model.Coupon;
import co.ohmygoods.coupon.model.CouponShopMapping;

import java.time.LocalDateTime;

public record IssueShopCouponResponse(Long couponId,
                                      String couponIssuerEmail,
                                      Long couponIssuedShopId,
                                      String couponIssuedShopName,
                                      String couponName,
                                      String couponCode,
                                      int couponMaxIssueCount,
                                      int couponMaxUsageCountPerAccount,
                                      String couponStatus,
                                      String couponDiscountType,
                                      int couponDiscountValue,
                                      LocalDateTime couponStartDate,
                                      LocalDateTime couponEndDate) {

    public static IssueShopCouponResponse from(Coupon issuedCoupon, CouponShopMapping couponShopMapping) {
        return new IssueShopCouponResponse(
                issuedCoupon.getId(),
                issuedCoupon.getIssuer().getEmail(),
                couponShopMapping.getApplyTargetShop().getId(),
                couponShopMapping.getApplyTargetShop().getName(),
                issuedCoupon.getName(),
                issuedCoupon.getCouponCode(),
                issuedCoupon.getMaxIssuableCount(),
                issuedCoupon.getMaxUsageCountPerAccount(),
                issuedCoupon.getStatus().name(),
                issuedCoupon.getDiscountType().name(),
                issuedCoupon.getDiscountValue(),
                issuedCoupon.getValidFrom(),
                issuedCoupon.getValidUntil());
    }
}
