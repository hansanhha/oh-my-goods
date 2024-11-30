package co.ohmygoods.coupon.model;

/**
 * UNLIMITED: 최대 발급 개수 및 계정 당 최대 사용 개수 제한 X
 * MAX_ISSUABLE_LIMITED: 최대 발급 개수 제한, 계정 당 최대 사용 개수 제한 X
 * PER_ACCOUNT_LIMITED: 최대 발급 개수 제한 X, 계정 당 최대 사용 개수 제한
 * FULL_LIMITED: 최대 발급 개수 및 계정 당 최대 사용 개수 제한
 */
public enum CouponLimitConditionType {

    UNLIMITED,
    MAX_ISSUABLE_LIMITED,
    PER_ACCOUNT_LIMITED,
    FULL_LIMITED;
}
