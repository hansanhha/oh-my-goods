package co.ohmygoods.coupon.exception;

import co.ohmygoods.global.exception.DomainException;

public class CouponException extends DomainException {

    public static final CouponException NOT_FOUND_COUPON = new CouponException(CouponError.NOT_FOUND_COUPON);
    public static final CouponException NOT_FOUND_COUPON_ISSUANCE_HISTORY = new CouponException(CouponError.COUPON_ISSUANCE_HISTORY_NOT_FOUND);

    public static final CouponException COUPON_ALREADY_ISSUED = new CouponException(CouponError.COUPON_ALREADY_USED);
    public static final CouponException COUPON_ALREADY_USED = new CouponException(CouponError.COUPON_ALREADY_USED);

    public static final CouponException COUPON_EXPIRED = new CouponException(CouponError.COUPON_EXPIRED);
    public static final CouponException EXCEED_COUPON_ISSUANCE_LIMIT = new CouponException(CouponError.EXCEED_COUPON_ISSUABLE_LIMIT);
    public static final CouponException EXHAUSTED_COUPON_ISSUANCE = new CouponException(CouponError.EXHAUSTED_COUPON);

    public static final CouponException THROW_INVALID_REQUIRED_FIELD = new CouponException(CouponError.INVALID_REQUIRED_FIELD);

    public CouponException(CouponError couponError) {
        super(couponError);
    }

    public static CouponException notFoundCoupon() {
        return NOT_FOUND_COUPON;
    }

    public static CouponException notFoundCouponIssuanceHistory() {
        return NOT_FOUND_COUPON_ISSUANCE_HISTORY;
    }


}
