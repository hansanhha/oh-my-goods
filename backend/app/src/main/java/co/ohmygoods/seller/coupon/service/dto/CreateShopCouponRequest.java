package co.ohmygoods.seller.coupon.service.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateShopCouponRequest(String issuerEmail,
                                      Long shopId,
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
