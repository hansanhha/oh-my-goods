package co.ohmygoods.coupon.service.admin.dto;

import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.shop.model.entity.Shop;

import java.time.LocalDateTime;

public record ShopCouponResponse(Long couponId,
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

    public static ShopCouponResponse from(Coupon issuedCoupon, Shop shop) {
        return new ShopCouponResponse(
                issuedCoupon.getId(),
                issuedCoupon.getIssuer().getEmail(),
                shop.getId(),
                shop.getName(),
                issuedCoupon.getName(),
                issuedCoupon.getCouponCode(),
                issuedCoupon.getIssuanceTarget().name(),
                issuedCoupon.getMaxIssuableQuantity(),
                issuedCoupon.getMaxUsageQuantityPerAccount(),
                issuedCoupon.getStatus().name(),
                issuedCoupon.getDiscountType().name(),
                issuedCoupon.getDiscountValue(),
                issuedCoupon.getCreatedAt(),
                issuedCoupon.getValidFrom(),
                issuedCoupon.getValidUntil());
    }
}
