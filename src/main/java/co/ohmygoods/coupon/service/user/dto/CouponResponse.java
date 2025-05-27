package co.ohmygoods.coupon.service.user.dto;

import co.ohmygoods.coupon.model.entity.Coupon;

import java.time.LocalDateTime;

public record CouponResponse(Long id,
                             String couponName,
                             String couponStatus,
                             String couponDiscountValue,
                             LocalDateTime couponStartDate,
                             LocalDateTime couponEndDate,
                             int minimumPurchasePrice,
                             int maximumDiscountPrice) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getStatus().name(),
                coupon.getDiscountValueAsString(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.getMinimumPurchasePriceForUsing(),
                coupon.getMaxDiscountPrice());
    }
}
