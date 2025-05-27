package co.ohmygoods.coupon.service.admin.dto;


import java.time.LocalDateTime;
import java.util.List;

import co.ohmygoods.coupon.controller.admin.dto.ShopCouponCreateWebRequest;


public record ShopCouponCreateRequest(String shopAdminMemberId,
                                      boolean isLimitedMaxIssueCount,
                                      int maxIssueCount,
                                      boolean isLimitedUsageCountPerAccount,
                                      int usageCountPerAccount,
                                      boolean isFixedDiscount,
                                      int discountValue,
                                      int minimumPurchasePrice,
                                      boolean isSpecificProductsIssuable,
                                      List<Long> issuableProductIds,
                                      String couponName,
                                      String couponCode,
                                      int maxDiscountPrice,
                                      LocalDateTime startDate,
                                      LocalDateTime endDate) {

    public static ShopCouponCreateRequest of(String adminMemberId, ShopCouponCreateWebRequest request) {
        return new ShopCouponCreateRequest(
            adminMemberId,
            request.isLimitedMaxIssueCount(), 
            request.maxIssueCount(), 
            request.isLimitedUsageCountPerAccount(), 
            request.usageCountPerAccount(), 
            request.isFixedDiscount(), 
            request.discountValue(), 
            request.minimumPurchasePrice(), 
            request.isSpecificProductsIssuable(),
            request.issuableProductIds(),
            request.couponName(), 
            request.couponCode(), 
            request.maxDiscountPrice(), 
            request.startDate(), 
            request.endDate());
    }
}
