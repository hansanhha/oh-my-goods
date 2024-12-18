package co.ohmygoods.coupon.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
import co.ohmygoods.coupon.model.vo.CouponIssuanceTarget;
import co.ohmygoods.coupon.model.vo.CouponIssueQuantityLimitType;
import co.ohmygoods.coupon.model.vo.CouponUsageStatus;
import org.springframework.stereotype.Component;

/*
todo
    쿠폰 발급 가능 계정 조건에 따른 검증 로직 구현
 */
class CouponValidationService {

    public static void validateBeforeUse(CouponUsageStatus status, int minimumPurchasePrice, int productPrice) {
        if (status.equals(CouponUsageStatus.USED)) {
            CouponException.throwAlreadyUsedCoupon();
        }

        if (minimumPurchasePrice < productPrice) {
            throw new CouponException();
        }
    }

    public static void validateBeforeIssue(Coupon coupon, OAuth2Account account, int issuedSameCouponCountToAccount) {
        validateIssuanceLimit(coupon, issuedSameCouponCountToAccount);
        validateIssueAccount(coupon, account);
    }

    public static void validateIssuanceLimit(Coupon coupon, int issuedSameCouponCountToAccount) {
        CouponIssueQuantityLimitType limitConditionType = coupon.getIssueQuantityLimitType();

        if (limitConditionType.equals(CouponIssueQuantityLimitType.FULL_LIMITED) ||
                limitConditionType.equals(CouponIssueQuantityLimitType.PER_ACCOUNT_LIMITED)) {
            validateMaxIssuedCountPerAccount(issuedSameCouponCountToAccount, coupon.getMaxUsageQuantityPerAccount());
        }

        if (limitConditionType.equals(CouponIssueQuantityLimitType.FULL_LIMITED) ||
                limitConditionType.equals(CouponIssueQuantityLimitType.MAX_ISSUABLE_LIMITED)) {
            validateOverTotalIssueCount(coupon);
        }

    }

    public static void validateIssueAccount(Coupon coupon, OAuth2Account account) {
        if (coupon.getIssuanceTarget().equals(CouponIssuanceTarget.SPECIFIC_ACCOUNTS)) {

        }
    }

    private static void validateMaxIssuedCountPerAccount(int issuedSameCouponCountToAccount, int maxIssuedCountPerAccount) {
        if (issuedSameCouponCountToAccount >= maxIssuedCountPerAccount) {
            CouponException.throwExceedMaxIssuedCountPerAccount();
        }
    }

    private static void validateOverTotalIssueCount(Coupon coupon) {
        if (coupon.getIssuedCount() >= coupon.getMaxIssuableQuantity()) {
            CouponException.throwExhausted();
        }
    }


}
