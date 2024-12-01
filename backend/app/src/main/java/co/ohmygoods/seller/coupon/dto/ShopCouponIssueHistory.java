package co.ohmygoods.seller.coupon.dto;

import co.ohmygoods.coupon.model.Coupon;
import co.ohmygoods.coupon.model.CouponDiscountType;

import java.time.LocalDateTime;

public record ShopCouponIssueHistory(String couponIssuerEmail,
                                     Long couponId,
                                     String couponName,
                                     String couponCode,
                                     String couponStatus,
                                     String couponTargetAccountType,
                                     String couponDiscountPrice,
                                     int couponMaxDiscountPrice,
                                     int couponCurrentIssuedCount,
                                     int couponMaxIssuableTotalCount,
                                     int couponMaxIssuableCountPerAccount,
                                     LocalDateTime couponCreateDate,
                                     LocalDateTime couponStartDate,
                                     LocalDateTime couponEndDate) {

    public static ShopCouponIssueHistory from(Coupon coupon) {
        String discountValue = String.valueOf(coupon.getDiscountValue());
        String selectedDiscountValue = coupon.getDiscountType().equals(CouponDiscountType.FIXED) ? discountValue : discountValue.concat("%");

        int maxIssuableTotalCount = coupon.getMaxIssuableCount() > 0 ? coupon.getMaxIssuableCount() : -1;
        int maxIssuableCountPerAccount = coupon.getMaxUsageCountPerAccount() > 0 ? coupon.getMaxUsageCountPerAccount() : -1;

        return new ShopCouponIssueHistory(coupon.getIssuer().getEmail(), coupon.getId(), coupon.getName(), coupon.getCouponCode(),
                coupon.getStatus().name(), coupon.getIssuanceTarget().name(), selectedDiscountValue,
                coupon.getMaxDiscountPrice(), coupon.getIssuedCount(), maxIssuableTotalCount, maxIssuableCountPerAccount,
                coupon.getCreatedAt(), coupon.getValidFrom(), coupon.getValidUntil());
    }
}
