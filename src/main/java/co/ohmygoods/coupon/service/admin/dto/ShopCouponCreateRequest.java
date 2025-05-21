package co.ohmygoods.coupon.service.admin.dto;


import java.time.LocalDateTime;
import java.util.List;

import co.ohmygoods.coupon.controller.admin.dto.ShopCouponCreateWebRequest;


public record ShopCouponCreateRequest(String sellerMemberId,
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

    public static ShopCouponCreateRequest of(String memberId, ShopCouponCreateWebRequest request) {
        return new ShopCouponCreateRequest(
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
