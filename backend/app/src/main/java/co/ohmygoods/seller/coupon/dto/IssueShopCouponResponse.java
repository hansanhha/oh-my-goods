package co.ohmygoods.seller.coupon.dto;

import co.ohmygoods.coupon.model.Coupon;
import co.ohmygoods.coupon.model.CouponShopMapping;
import co.ohmygoods.shop.entity.Shop;

import java.time.LocalDateTime;

public record IssueShopCouponResponse(Long couponId,
                                      String couponIssuerEmail,
                                      Long couponIssuedShopId,
                                      String couponIssuedShopName,
                                      String couponName,
                                      String couponCode,
                                      String couponTargetAccountType,
                                      int couponMaxIssueCount,
                                      int couponMaxUsageCountPerAccount,
                                      String couponStatus,
                                      String couponDiscountType,
                                      int couponDiscountValue,
                                      LocalDateTime couponCreateDate,
                                      LocalDateTime couponStartDate,
                                      LocalDateTime couponEndDate) {

    public static IssueShopCouponResponse from(Coupon issuedCoupon, Shop shop) {
        return new IssueShopCouponResponse(
                issuedCoupon.getId(),
                issuedCoupon.getIssuer().getEmail(),
                shop.getId(),
                shop.getName(),
                issuedCoupon.getName(),
                issuedCoupon.getCouponCode(),
                issuedCoupon.getIssuanceTarget().name(),
                issuedCoupon.getMaxIssuableCount(),
                issuedCoupon.getMaxUsageCountPerAccount(),
                issuedCoupon.getStatus().name(),
                issuedCoupon.getDiscountType().name(),
                issuedCoupon.getDiscountValue(),
                issuedCoupon.getCreatedAt(),
                issuedCoupon.getValidFrom(),
                issuedCoupon.getValidUntil());
    }
}
