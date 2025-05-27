package co.ohmygoods.coupon.model.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CouponDiscountCalculator {

    public static int calculate(CouponDiscountType type, int discountValue, int maxDiscountPrice, int productPrice) {
        double subtractedPrice = type.equals(CouponDiscountType.FIXED)
                ? productPrice - discountValue
                : productPrice - (productPrice * getPercentageValue(discountValue));

        return finalCalculate(subtractedPrice, maxDiscountPrice);
    }

    private static int finalCalculate(double simpleCalculatedPrice, int couponMaxDiscountPrice) {
        BigDecimal couponAppliedPrice = BigDecimal.valueOf(simpleCalculatedPrice).setScale(0, RoundingMode.HALF_UP);
        return Math.min(couponAppliedPrice.intValue(), couponMaxDiscountPrice);
    }

    private static double getPercentageValue(int discountValue) {
        if (discountValue ==  100) {
            return 1d;
        }

        return (double) discountValue / 100;
    }
}
