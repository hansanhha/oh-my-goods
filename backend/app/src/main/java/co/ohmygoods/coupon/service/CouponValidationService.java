package co.ohmygoods.coupon.service;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.entity.Coupon;
import co.ohmygoods.coupon.model.entity.CouponUsageHistory;
import co.ohmygoods.coupon.model.vo.CouponIssuanceTarget;
import co.ohmygoods.coupon.model.vo.CouponLimitConditionType;
import co.ohmygoods.coupon.model.vo.CouponUsageStatus;
import org.springframework.stereotype.Component;

/*
todo
    쿠폰 발급 가능 계정 조건에 따른 검증 로직 구현
 */
@Component
public class CouponValidationService {

    public void validateBeforeUse(CouponUsageHistory history) {
        if (history.getCouponUsageStatus().equals(CouponUsageStatus.USED)) {
            CouponException.throwAlreadyUsedCoupon();
        }
    }

    public void validateBeforeIssue(Coupon coupon, OAuth2Account account, int issuedSameCouponCountToAccount) {
        validateIssuanceLimit(coupon, issuedSameCouponCountToAccount);
        validateIssueAccount(coupon, account);
    }

    public void validateIssuanceLimit(Coupon coupon, int issuedSameCouponCountToAccount) {
        CouponLimitConditionType limitConditionType = coupon.getLimitConditionType();

        if (limitConditionType.equals(CouponLimitConditionType.FULL_LIMITED) ||
                limitConditionType.equals(CouponLimitConditionType.PER_ACCOUNT_LIMITED)) {
            validateMaxIssuedCountPerAccount(issuedSameCouponCountToAccount, coupon.getMaxUsageCountPerAccount());
        }

        if (limitConditionType.equals(CouponLimitConditionType.FULL_LIMITED) ||
                limitConditionType.equals(CouponLimitConditionType.MAX_ISSUABLE_LIMITED)) {
            validateOverTotalIssueCount(coupon);
        }

    }

    public void validateIssueAccount(Coupon coupon, OAuth2Account account) {
        if (coupon.getIssuanceTarget().equals(CouponIssuanceTarget.SPECIFIC_ACCOUNTS)) {

        }
    }

    private void validateMaxIssuedCountPerAccount(int issuedSameCouponCountToAccount, int maxIssuedCountPerAccount) {
        if (issuedSameCouponCountToAccount >= maxIssuedCountPerAccount) {
            CouponException.throwExceedMaxIssuedCountPerAccount();
        }
    }

    private void validateOverTotalIssueCount(Coupon coupon) {
        if (coupon.getIssuedCount() >= coupon.getMaxIssuableCount()) {
            CouponException.throwExhausted();
        }
    }


}
