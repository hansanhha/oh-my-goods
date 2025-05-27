package co.ohmygoods.coupon.model.service;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.vo.CouponIssueTarget;
import co.ohmygoods.coupon.model.vo.CouponIssueQuantityLimitType;


public class CouponValidationService {

    public static void validateIssuable(Coupon coupon, Account account, int issuedSameCouponCountToAccount) {
        validateIssueCountLimit(coupon, issuedSameCouponCountToAccount);
        validateIssueAccount(coupon, account);
    }

    public static void validateIssueCountLimit(Coupon coupon, int issuedSameCouponCountToAccount) {
        CouponIssueQuantityLimitType limitConditionType = coupon.getIssueQuantityLimitType();

        if (limitConditionType.equals(CouponIssueQuantityLimitType.FULL_LIMITED) ||
                limitConditionType.equals(CouponIssueQuantityLimitType.PER_ACCOUNT_LIMITED)) {
            validateMaxIssuedCountPerAccount(issuedSameCouponCountToAccount, coupon.getMaximumQuantityPerAccount());
        }

        if (limitConditionType.equals(CouponIssueQuantityLimitType.FULL_LIMITED) ||
                limitConditionType.equals(CouponIssueQuantityLimitType.MAX_ISSUABLE_LIMITED)) {
            validateOverTotalIssueCount(coupon);
        }

    }

    public static void validateIssueAccount(Coupon coupon, Account account) {
        if (coupon.getIssueTarget().equals(CouponIssueTarget.SPECIFIC_ACCOUNTS)) {

        }
    }

    private static void validateMaxIssuedCountPerAccount(int issuedSameCouponCountToAccount, int maxIssuedCountPerAccount) {
        if (issuedSameCouponCountToAccount >= maxIssuedCountPerAccount) {
            throw CouponException.EXCEED_COUPON_ISSUANCE_LIMIT;
        }
    }

    private static void validateOverTotalIssueCount(Coupon coupon) {
        if (coupon.getIssuedCount() >= coupon.getMaxIssuableQuantity()) {
            throw CouponException.EXHAUSTED_COUPON_ISSUANCE;
        }
    }


}
