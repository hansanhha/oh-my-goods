package co.ohmygoods.seller.coupon.service.dto;


import java.time.LocalDateTime;
import java.util.List;

import co.ohmygoods.seller.coupon.controller.dto.CreateShopCouponWebRequest;


public record CreateShopCouponRequest(String sellerMemberId,
                                      boolean isLimitedMaxIssueCount,
                                      int maxIssueCount,
                                      boolean isLimitedUsageCountPerAccount,
                                      int usageCountPerAccount,
                                      boolean isFixedDiscount,
                                      int discountValue,
                                      int minimumPurchasePrice,
                                      boolean isApplicableSpecificProducts,
                                      List<Long> applicableProductIds,
                                      String couponName,
                                      String couponCode,
                                      int maxDiscountPrice,
                                      LocalDateTime startDate,
                                      LocalDateTime endDate) {

    public static CreateShopCouponRequest of(String memberId, CreateShopCouponWebRequest request) {
        return new CreateShopCouponRequest(
            memberId, 
            request.isLimitedMaxIssueCount(), 
            request.maxIssueCount(), 
            request.isLimitedUsageCountPerAccount(), 
            request.usageCountPerAccount(), 
            request.isFixedDiscount(), 
            request.discountValue(), 
            request.minimumPurchasePrice(), 
            request.isApplicableSpecificProducts(), 
            request.applicableProductIds(), 
            request.couponName(), 
            request.couponCode(), 
            request.maxDiscountPrice(), 
            request.startDate(), 
            request.endDate());
    }
}
