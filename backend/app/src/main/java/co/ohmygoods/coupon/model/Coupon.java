package co.ohmygoods.coupon.model;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private CouponLimitConditionType limitConditionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponUsageProductScope usageProductScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponIssuanceTarget issuanceTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDiscountType discountType;

    @Column(nullable = false)
    private int maxIssuableCount;

    @Column(nullable = false)
    private int maxUsageCountPerAccount;

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

    public static class CouponBuilder {
        private OAuth2Account issuer;
        private String name;
        private String couponCode;
        private CouponType type;
        private CouponLimitConditionType limitConditionType;
        private CouponUsageProductScope usageProductScope;
        private CouponStatus status;
        private CouponIssuanceTarget issuanceTarget;
        private CouponDiscountType discountType;
        private Integer maxIssuableCount;
        private Integer maxUsageCountPerAccount;
        private Integer issuedCount;
        private Integer discountValue;
        private Integer maxDiscountPrice;
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

        public CouponBuilder limitedType(CouponLimitConditionType limitConditionType) {
            this.limitConditionType = limitConditionType;
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

        public CouponBuilder maxIssuableCount(int maxIssuableCount) {
            this.maxIssuableCount = maxIssuableCount;
            return this;
        }

        public CouponBuilder maxUsageCountPerAccount(int maxUsageCountPerAccount) {
            this.maxUsageCountPerAccount = maxUsageCountPerAccount;
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

        public Coupon buildGeneralCoupon(CouponUsageProductScope detailType) {
            return buildCoupon(CouponType.GENERAL_COUPON, detailType);
        }

        public Coupon buildShopCoupon(boolean isApplicableAllProducts) {
            CouponUsageProductScope couponUsageProductScope = isApplicableAllProducts
                    ? CouponUsageProductScope.SHOP_ALL_PRODUCTS
                    : CouponUsageProductScope.SHOP_SPECIFIC_PRODUCTS;

            return buildCoupon(CouponType.SHOP_COUPON, couponUsageProductScope);
        }

        private Coupon buildCoupon(CouponType type, CouponUsageProductScope couponUsageProductScope) {
            this.type = type;
            this.usageProductScope = couponUsageProductScope;
            this.status = CouponStatus.ISSUED;
            this.issuedCount = 0;

            validateCouponInfo();
            validateLimitCondition();
            validateIssueAuthority(issuer, type);
            validateDiscountValue(discountType, discountValue);

            return new Coupon(0L, issuer, name, couponCode, type, limitConditionType,
                    couponUsageProductScope, status, issuanceTarget, discountType, maxIssuableCount,
                    maxUsageCountPerAccount, issuedCount, discountValue, maxDiscountPrice, validFrom, validUntil);
        }

        private void validateCouponInfo() {
            if (issuer == null || name == null || couponCode == null || type == null || usageProductScope == null ||
                    status == null || issuanceTarget == null || maxIssuableCount == null || maxUsageCountPerAccount == null ||
                    issuedCount == null || discountType == null || validFrom == null || validUntil == null) {
                CouponException.throwInvalidCouponInfo();
            }
        }

        private void validateLimitCondition() {
            if (limitConditionType.equals(CouponLimitConditionType.FULL_LIMITED)) {
                validateMaxUsageCountPerAccount();
                validateMaxIssuableCount();
            } else if (limitConditionType.equals(CouponLimitConditionType.MAX_ISSUABLE_LIMITED)) {
                validateMaxUsageCountPerAccount();
            } else if (limitConditionType.equals(CouponLimitConditionType.PER_ACCOUNT_LIMITED)) {
                validateMaxIssuableCount();
            }
        }

        private void validateMaxIssuableCount() {
            if (maxIssuableCount <= 0) {
                CouponException.throwBadLimitCondition();
            }
        }

        private void validateMaxUsageCountPerAccount() {
            if (maxUsageCountPerAccount <= 0) {
                CouponException.throwBadLimitCondition();
            }
        }


        private static void validateDiscountValue(CouponDiscountType discountType, int discountValue) {
            if (discountValue <= 0 || (discountType.equals(CouponDiscountType.PERCENTAGE) && discountValue > MAX_DISCOUNT_PERCENTAGE)) {
                CouponException.throwInvalidDiscountValue();
            }
        }

        private static void validateIssueAuthority(OAuth2Account account, CouponType couponType) {
            switch (couponType) {
                case GENERAL_COUPON -> {
                    if (!account.canIssueGeneralCoupon()) {
                        CouponException.throwNoAuthorityIssuanceCoupon();
                    }
                }
                case SHOP_COUPON -> {
                    if (!account.canIssueShopCoupon()) {
                        CouponException.throwNoAuthorityIssuanceCoupon();
                    }
                }
                default -> {}
            };
        }
    }

    public void destroy() {
        if (status.equals(CouponStatus.DESTROYED) || status.equals(CouponStatus.SOLDOUT)) {
            return;
        }

        status = CouponStatus.DESTROYED;
    }
}
