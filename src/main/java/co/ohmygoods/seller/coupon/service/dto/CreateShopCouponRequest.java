package co.ohmygoods.seller.coupon.service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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
}
