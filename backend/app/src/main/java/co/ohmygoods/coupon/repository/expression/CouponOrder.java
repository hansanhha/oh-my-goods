package co.ohmygoods.coupon.repository.expression;

import co.ohmygoods.coupon.model.entity.QCoupon;
import com.querydsl.core.types.OrderSpecifier;

import java.time.LocalDateTime;

public class CouponOrder {

    public static OrderSpecifier<LocalDateTime> sortByCreatedAtDesc(QCoupon coupon) {
        return coupon.createdAt.desc();
    }
}
