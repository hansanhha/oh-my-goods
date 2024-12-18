package co.ohmygoods.coupon.model.vo;

/**
 * UNLIMITED: 최대 발급 개수 및 계정 당 최대 사용 개수 제한 X
 * MAX_ISSUABLE_LIMITED: 최대 발급 개수 제한, 계정 당 최대 사용 개수 제한 X
 * PER_ACCOUNT_LIMITED: 최대 발급 개수 제한 X, 계정 당 최대 사용 개수 제한
 * FULL_LIMITED: 최대 발급 개수 및 계정 당 최대 사용 개수 제한
 */
public enum CouponIssueQuantityLimitType {

    UNLIMITED,
    MAX_ISSUABLE_LIMITED,
    PER_ACCOUNT_LIMITED,
    FULL_LIMITED;

    public static CouponIssueQuantityLimitType get(boolean limitedMaxIssueCount, boolean limitedUsageCountPerAccount) {
        if (limitedMaxIssueCount && limitedUsageCountPerAccount) {
            return CouponIssueQuantityLimitType.FULL_LIMITED;
        } else if (limitedMaxIssueCount) {
            return CouponIssueQuantityLimitType.MAX_ISSUABLE_LIMITED;
        } else if (limitedUsageCountPerAccount) {
            return CouponIssueQuantityLimitType.PER_ACCOUNT_LIMITED;
        } else {
            return CouponIssueQuantityLimitType.UNLIMITED;
        }
    }
}
