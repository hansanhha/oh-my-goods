package co.ohmygoods.seller.coupon.dto;

import java.time.LocalDateTime;
import java.util.List;

public record IssueShopCouponRequest(String issuerEmail,
                                     Long shopId,
                                     boolean isLimitedMaxIssueCount,
                                     int maxIssueCount,
                                     boolean isLimitedUsageCountPerAccount,
                                     int usageCountPerAccount,
                                     boolean isFixedDiscount,
                                     int discountValue,
                                     boolean isApplicableSpecificProducts,
                                     List<Long> applicableProductIds,
                                     String couponName,
                                     String couponCode,
                                     int maxDiscountPrice,
                                     LocalDateTime startDate,
                                     LocalDateTime endDate) {
}
