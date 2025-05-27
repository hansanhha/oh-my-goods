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

    public static ShopCouponResponse from(Coupon createdCoupon, Shop shop) {
        return new ShopCouponResponse(
                createdCoupon.getId(),
                createdCoupon.getIssuer().getEmail(),
                shop.getId(),
                shop.getName(),
                createdCoupon.getName(),
                createdCoupon.getCouponCode(),
                createdCoupon.getIssueTarget().name(),
                createdCoupon.getMaxIssuableQuantity(),
                createdCoupon.getMaximumQuantityPerAccount(),
                createdCoupon.getStatus().name(),
                createdCoupon.getDiscountType().name(),
                createdCoupon.getDiscountValue(),
                createdCoupon.getCreatedAt(),
                createdCoupon.getValidFrom(),
                createdCoupon.getValidUntil());
    }
}
