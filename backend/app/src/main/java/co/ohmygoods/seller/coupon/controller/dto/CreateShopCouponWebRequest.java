package co.ohmygoods.seller.coupon.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record CreateShopCouponWebRequest(@NotNull(message = "쿠폰의 최대 발급 개수 제한 여부를 결정해주세요")
                                         boolean isLimitedMaxIssueCount,
                                         @Positive(message = "올바르지 않은 쿠폰 최대 발급 개수입니다")
                                         int maxIssueCount,
                                         @NotNull(message = "계정 당 쿠폰 발급 제한 여부를 결정해주세요")
                                         boolean isLimitedUsageCountPerAccount,
                                         @Positive(message = "올바르지 않은 계정 당 최대 쿠폰 발급 개수입니다")
                                         int usageCountPerAccount,
                                         @NotNull(message = "고정 할인 금액 여부를 결정해주세요")
                                         boolean isFixedDiscount,
                                         @NotNull(message = "올바르지 않은 할인 금액입니다") @Positive(message = "올바르지 않은 할인 금액입니다")
                                         int discountValue,
                                         @Positive(message = "올바르지 않은 최소 구매 금액 조건입니다")
                                         int minimumPurchasePrice,
                                         @NotNull(message = "쿠폰 적용 대상 상품 제한 여부를 결정해주세요")
                                         boolean isApplicableSpecificProducts,
                                         List<Long> applicableProductIds,
                                         @NotEmpty(message = "올바르지 않은 쿠폰 이름입니다")
                                         String couponName,
                                         @NotEmpty(message = "올바르지 않은 쿠폰 코드입니다")
                                         String couponCode,
                                         @Positive(message = "올바르지 않은 최대 할인 금액입니다")
                                         int maxDiscountPrice,
                                         @NotNull(message = "올바르지 않은 쿠폰 사용 가능 시작 기간입니다")
                                         LocalDateTime startDate,
                                         @NotNull(message = "올바르지 않은 쿠폰 사용 가능 종료 기간입니다")
                                         LocalDateTime endDate) {
}
