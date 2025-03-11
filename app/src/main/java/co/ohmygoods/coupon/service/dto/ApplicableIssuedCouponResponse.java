package co.ohmygoods.coupon.service.dto;

import co.ohmygoods.coupon.model.entity.Coupon;

import java.time.LocalDateTime;

public record ApplicableIssuedCouponResponse(String couponName,
                                             String couponStatus,
                                             String couponDiscountValue,
                                             LocalDateTime couponStartDate,
                                             LocalDateTime couponEndDate,
                                             int minimumPurchasePrice,
                                             int maximumDiscountPrice) {
    public static ApplicableIssuedCouponResponse from(Coupon coupon) {
        return new ApplicableIssuedCouponResponse(coupon.getName(),
                coupon.getStatus().name(),
                coupon.getDiscountValueAsString(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.getMinimumPurchasePriceForApply(),
                coupon.getMaxDiscountPrice());
    }
}
