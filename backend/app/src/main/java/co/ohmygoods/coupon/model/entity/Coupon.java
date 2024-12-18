package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.vo.*;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseEntity {

    private static final int MAX_DISCOUNT_PERCENTAGE = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id")
    private OAuth2Account issuer;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponIssueQuantityLimitType issueQuantityLimitType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponApplicableProductScope applicableProductScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponIssuanceTarget issuanceTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDiscountType discountType;

    private int maxIssuableQuantity;

    private int maxUsageQuantityPerAccount;

    @Column(nullable = false)
    private int issuedCount;

    @Column(nullable = false)
    private int discountValue;

    private int maxDiscountPrice;

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    public static CouponBuilder builder() {
        return new CouponBuilder();
    }

    public void issue() {
        issuedCount++;

        if ((issueQuantityLimitType.equals(CouponIssueQuantityLimitType.FULL_LIMITED)
                || issueQuantityLimitType.equals(CouponIssueQuantityLimitType.MAX_ISSUABLE_LIMITED))
                && maxIssuableQuantity < issuedCount) {
            CouponException.throwExhausted();
        }
    }

    public void destroy(OAuth2Account account) {
        if (!account.canDestroyShopCoupon()) {
            CouponException.throwInvalidCouponAuthority();
        }

        if (status.equals(CouponStatus.DESTROYED) || status.equals(CouponStatus.SOLDOUT)) {
            return;
        }

        status = CouponStatus.DESTROYED;
    }

    public String getDiscountValueAsString() {
        if (discountType.equals(CouponDiscountType.PERCENTAGE)) {
            return String.valueOf(discountValue).concat("%");
        }

        return String.valueOf(discountValue);
    }

    public int calculate(int productPrice) {
        double subtractedPrice = discountType.equals(CouponDiscountType.FIXED)
                ? productPrice - discountValue
                : productPrice - (productPrice * getPercentageValue(discountValue));

        return finalCalculate(subtractedPrice, maxDiscountPrice);
    }

    private int finalCalculate(double simpleCalculatedPrice, int couponMaxDiscountPrice) {
        BigDecimal couponAppliedPrice = BigDecimal.valueOf(simpleCalculatedPrice).setScale(0, RoundingMode.HALF_UP);
        return Math.min(couponAppliedPrice.intValue(), couponMaxDiscountPrice);
    }

    private double getPercentageValue(int discountValue) {
        if (discountValue ==  100) {
            return 1d;
        }

        return (double) discountValue / 100;
    }

    public static class CouponBuilder {
        private OAuth2Account issuer;
        private String name;
        private String couponCode;
        private CouponType type;
        private CouponIssueQuantityLimitType issueQuantityLimitType;
        private CouponApplicableProductScope usageProductScope;
        private CouponStatus status;
        private CouponIssuanceTarget issuanceTarget;
        private CouponDiscountType discountType;
        private Integer discountValue;
        private Integer maxDiscountPrice;
        private Integer maxIssuableQuantity;
        private Integer maxUsageQuantityPerAccount;
        private Integer issuedCount;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;

        private CouponBuilder() {

        }

        public CouponBuilder issuer(OAuth2Account issuer) {
            this.issuer = issuer;
            return this;
        }

        public CouponBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CouponBuilder couponCode(String couponCode) {
            this.couponCode = couponCode;
            return this;
        }

        public CouponBuilder issueQuantityLimitType(CouponIssueQuantityLimitType issueQuantityLimitType) {
            this.issueQuantityLimitType = issueQuantityLimitType;
            return this;
        }

        public CouponBuilder issuanceTarget(CouponIssuanceTarget issuanceTarget) {
            this.issuanceTarget = issuanceTarget;
            return this;
        }

        public CouponBuilder discountType(CouponDiscountType discountType) {
            this.discountType = discountType;
            return this;
        }

        public CouponBuilder maxIssuableQuantity(int maxIssuableQuantity) {
            this.maxIssuableQuantity = maxIssuableQuantity;
            return this;
        }

        public CouponBuilder maxUsageQuantityPerAccount(int maxUsageQuantityPerAccount) {
            this.maxUsageQuantityPerAccount = maxUsageQuantityPerAccount;
            return this;
        }

        public CouponBuilder discountValue(int discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public CouponBuilder maxDiscountPrice(int maxDiscountPrice) {
            this.maxDiscountPrice = maxDiscountPrice;
            return this;
        }

        public CouponBuilder validFrom(LocalDateTime validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public CouponBuilder validUntil(LocalDateTime validUntil) {
            this.validUntil = validUntil;
            return this;
        }

        public Coupon buildGeneralCoupon(CouponApplicableProductScope detailType) {
            return buildCoupon(CouponType.GENERAL_COUPON, detailType);
        }

        public Coupon buildShopCoupon(boolean isApplicableSpecificProducts) {
            CouponApplicableProductScope couponApplicableProductScope = isApplicableSpecificProducts
                    ? CouponApplicableProductScope.SHOP_SPECIFIC_PRODUCTS
                    : CouponApplicableProductScope.SHOP_ALL_PRODUCTS;

            return buildCoupon(CouponType.SHOP_COUPON, couponApplicableProductScope);
        }

        private Coupon buildCoupon(CouponType type, CouponApplicableProductScope couponApplicableProductScope) {
            this.type = type;
            this.usageProductScope = couponApplicableProductScope;
            this.status = CouponStatus.ISSUED;
            this.issuedCount = 0;

            validateRequiredField();
            validateLimitCondition();
            validateCouponIssuanceAuthority(issuer, type);
            validateDiscountValue(discountType, discountValue);

            return new Coupon(0L, issuer, name, couponCode, type, issueQuantityLimitType,
                    couponApplicableProductScope, status, issuanceTarget, discountType, maxIssuableQuantity,
                    maxUsageQuantityPerAccount, issuedCount, discountValue, maxDiscountPrice, validFrom, validUntil);
        }

        private void validateRequiredField() {
            if (issuer == null || name == null || couponCode == null || type == null ||
                    usageProductScope == null || status == null || issuanceTarget == null ||
                    issuedCount == null || discountType == null || validFrom == null || validUntil == null) {
                CouponException.throwInvalidCouponInfo();
            }
        }

        private void validateLimitCondition() {
            if (issueQuantityLimitType.equals(CouponIssueQuantityLimitType.FULL_LIMITED)) {
                validateMaxUsageCountPerAccount();
                validateMaxIssuableCount();
            } else if (issueQuantityLimitType.equals(CouponIssueQuantityLimitType.MAX_ISSUABLE_LIMITED)) {
                validateMaxUsageCountPerAccount();
            } else if (issueQuantityLimitType.equals(CouponIssueQuantityLimitType.PER_ACCOUNT_LIMITED)) {
                validateMaxIssuableCount();
            }
        }

        private void validateMaxIssuableCount() {
            if (maxIssuableQuantity <= 0) {
                CouponException.throwBadLimitCondition();
            }
        }

        private void validateMaxUsageCountPerAccount() {
            if (maxUsageQuantityPerAccount <= 0) {
                CouponException.throwBadLimitCondition();
            }
        }


        private static void validateDiscountValue(CouponDiscountType discountType, int discountValue) {
            if (discountValue <= 0 || (discountType.equals(CouponDiscountType.PERCENTAGE) && discountValue > MAX_DISCOUNT_PERCENTAGE)) {
                CouponException.throwInvalidDiscountValue();
            }
        }

        private static void validateCouponIssuanceAuthority(OAuth2Account account, CouponType couponType) {
            switch (couponType) {
                case GENERAL_COUPON -> {
                    if (!account.canIssueGeneralCoupon()) {
                        CouponException.throwInvalidCouponAuthority();
                    }
                }
                case SHOP_COUPON -> {
                    if (!account.canIssueShopCoupon()) {
                        CouponException.throwInvalidCouponAuthority();
                    }
                }
                default -> {}
            };
        }
    }

}
