package co.ohmygoods.coupon.exception;

public class CouponException extends RuntimeException {

    public CouponException() {
    }

    public CouponException(String message) {
        super(message);
    }

    public static void throwInvalidCouponAuthority() {
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

    public static CouponException notFoundCoupon() {
        return new CouponException();
    }

    public static CouponException notFoundAccount() {
        return new CouponException();
    }

    public static void throwBadLimitCondition() {
        throw new CouponException();
    }

    public static void throwExceedMaxIssuedCountPerAccount() {
        throw new CouponException();
    }

    public static void throwExhausted() {
        throw new CouponException();
    }

    public static void throwAlreadyUsedCoupon() {
        throw new CouponException();
    }

    public static CouponException notFoundCouponIssuanceHistory() {
        return new CouponException();
    }

    public static CouponException notFoundProduct() {
        return new CouponException();
    }
}
