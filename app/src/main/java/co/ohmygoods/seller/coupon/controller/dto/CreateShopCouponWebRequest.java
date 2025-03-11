package co.ohmygoods.seller.coupon.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record CreateShopCouponWebRequest(

        @Schema(description = "쿠폰 최대 발급 개수 제한 여부")
        @NotNull(message = "쿠폰의 최대 발급 개수 제한 여부를 결정해주세요")
        boolean isLimitedMaxIssueCount,

        @Schema(description = "쿠폰 최대 발급 개수")
        @Positive(message = "올바르지 않은 쿠폰 최대 발급 개수입니다")
        int maxIssueCount,

        @Schema(description = "계정 당 쿠폰 최대 발급 개수 제한 여부")
        @NotNull(message = "계정 당 쿠폰 발급 제한 여부를 결정해주세요")
        boolean isLimitedUsageCountPerAccount,

        @Schema(description = "계정 당 쿠폰 최대 발급 개수")
        @Positive(message = "올바르지 않은 계정 당 최대 쿠폰 발급 개수입니다")
        int usageCountPerAccount,

        @Schema(description = "고정 할인 금액 여부. false일 경우 쿠폰 적용 시 할인 값이 정률로 적용됩니다")
        @NotNull(message = "고정 할인 금액 여부를 결정해주세요")
        boolean isFixedDiscount,

        @Schema(description = "할인 금액")
        @NotNull(message = "올바르지 않은 할인 금액입니다") @Positive(message = "올바르지 않은 할인 금액입니다")
        int discountValue,

        @Schema(description = "최소 구매 금액 조건")
        @Positive(message = "올바르지 않은 최소 구매 금액 조건입니다")
        int minimumPurchasePrice,

        @Schema(description = "쿠폰 적용 대상 상품 제한 여부")
        @NotNull(message = "쿠폰 적용 대상 상품 제한 여부를 결정해주세요")
        boolean isApplicableSpecificProducts,

        @Schema(description = "쿠폰을 적용할 수 있는 상품의 아이디")
        List<Long> applicableProductIds,

        @Schema(description = "쿠폰 이름")
        @NotEmpty(message = "올바르지 않은 쿠폰 이름입니다")
        String couponName,

        @Schema(description = "쿠폰 코드")
        @NotEmpty(message = "올바르지 않은 쿠폰 코드입니다")
        String couponCode,

        @Schema(description = "쿠폰의 최대 할인 금액")
        @Positive(message = "올바르지 않은 최대 할인 금액입니다")
        int maxDiscountPrice,

        @Schema(description = "쿠폰 적용 가능 시작 날짜")
        @NotNull(message = "올바르지 않은 쿠폰 사용 가능 시작 기간입니다")
        LocalDateTime startDate,

        @Schema(description = "쿠폰 적용 가능 종료 날짜")
        @NotNull(message = "올바르지 않은 쿠폰 사용 가능 종료 기간입니다")
        LocalDateTime endDate) {
}
