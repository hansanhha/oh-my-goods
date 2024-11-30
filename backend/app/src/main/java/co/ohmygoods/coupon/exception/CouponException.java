package co.ohmygoods.coupon.exception;

public class CouponException extends RuntimeException {

    public CouponException() {
    }

    public CouponException(String message) {
        super(message);
    }

    public static void throwNoAuthorityIssuanceCoupon() {
        throw new CouponException();
    }

    public static void throwInvalidDiscountValue() {
        throw new CouponException();
    }

    public static void throwInvalidCouponInfo() {
        throw new CouponException();
    }

    public static CouponException notFoundIssuer() {
        return new CouponException();
    }

    public static CouponException notFoundShop() {
        return new CouponException();
    }

    public static void throwBadLimitCondition() {
        throw new CouponException();
    }
}
