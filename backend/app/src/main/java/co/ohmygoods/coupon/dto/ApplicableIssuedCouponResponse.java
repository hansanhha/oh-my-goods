package co.ohmygoods.coupon.dto;

import co.ohmygoods.coupon.model.entity.Coupon;

import java.time.LocalDateTime;

public record ApplicableIssuedCouponResponse(String couponName,
                                             String couponStatus,
                                             String couponDiscountValue,
                                             LocalDateTime couponStartDate,
                                             LocalDateTime couponEndDate,
                                             int maximumDiscountPriceForProduct) {
    public static ApplicableIssuedCouponResponse from(Coupon coupon, Integer maximumDiscountPriceForProduct) {
        return new ApplicableIssuedCouponResponse(coupon.getName(),
                coupon.getStatus().name(),
                coupon.getDiscountValueAsString(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                maximumDiscountPriceForProduct == null || maximumDiscountPriceForProduct <= 0
                        ? -1
                        : maximumDiscountPriceForProduct);
    }
}
