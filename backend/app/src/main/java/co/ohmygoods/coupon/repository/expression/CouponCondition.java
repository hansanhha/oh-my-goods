package co.ohmygoods.coupon.repository.expression;

import co.ohmygoods.coupon.model.entity.QCouponHistory;
import co.ohmygoods.coupon.model.vo.CouponHistoryStatus;
import com.querydsl.core.types.dsl.BooleanExpression;

public class CouponCondition {

    public static BooleanExpression isEqualStatus(QCouponHistory couponHistory, CouponHistoryStatus usageStatus) {
        return couponHistory.couponHistoryStatus.eq(usageStatus);
    }

}
