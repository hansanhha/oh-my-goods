package co.ohmygoods.coupon.model.vo;


import java.math.BigDecimal;
import java.math.RoundingMode;


public class CouponDiscountCalculator {

    public static int calculate(CouponDiscountType type, int discountValue, int couponMaximumDiscountPrice, int productPrice) {
        int discountedProductPrice = type.equals(CouponDiscountType.FIXED)
                ? productPrice - discountValue
                : BigDecimal.valueOf(productPrice - (productPrice * getPercentageValue(discountValue))).setScale(0, RoundingMode.HALF_UP).intValue();

        int couponDiscountPrice = productPrice - discountedProductPrice;

        if (couponMaximumDiscountPrice > 0) return Math.min(couponDiscountPrice, couponMaximumDiscountPrice);
        else return couponDiscountPrice;
    }

    private static double getPercentageValue(int discountValue) {
        if (discountValue ==  100) {
            return 1d;
        }

        return (double) discountValue / 100;
    }
}
