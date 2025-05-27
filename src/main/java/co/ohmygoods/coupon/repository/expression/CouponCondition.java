package co.ohmygoods.coupon.repository.expression;

import co.ohmygoods.coupon.model.entity.QCouponUsingHistory;
import co.ohmygoods.coupon.model.vo.CouponUsingStatus;
import com.querydsl.core.types.dsl.BooleanExpression;

public class CouponCondition {

    public static BooleanExpression isEqualStatus(QCouponUsingHistory couponHistory, CouponUsingStatus usageStatus) {
        return couponHistory.couponUsingStatus.eq(usageStatus);
    }

}
