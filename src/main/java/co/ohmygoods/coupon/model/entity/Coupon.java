package co.ohmygoods.coupon.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.coupon.exception.CouponException;
import co.ohmygoods.coupon.model.vo.*;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.model.entity.Shop;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static co.ohmygoods.coupon.model.vo.CouponIssueQuantityLimitType.*;

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
    private Account issuer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

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
    private CouponUsableScope usableScope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponIssueTarget issueTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDiscountType discountType;

    private int maxIssuableQuantity;

    private int maximumQuantityPerAccount;

    @Column(nullable = false)
    private int issuedCount;

    @Column(nullable = false)
    private int discountValue;

    private int minimumPurchasePriceForUsing;

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
    }

    public String getDiscountValueAsString() {
        if (discountType.equals(CouponDiscountType.PERCENTAGE)) {
            return String.valueOf(discountValue).concat("%");
        }

        return String.valueOf(discountValue);
    }

    public boolean requireIssueValidation() {
        return !issueQuantityLimitType.equals(UNLIMITED);
    }

    public static class CouponBuilder {
        private Account issuer;
        private Shop shop;
        private String name;
        private String couponCode;
        private CouponType type;
        private CouponIssueQuantityLimitType issueQuantityLimitType;
        private CouponUsableScope usageProductScope;
        private CouponStatus status;
        private CouponIssueTarget issuanceTarget;
        private CouponDiscountType discountType;
        private Integer discountValue;
        private Integer minimumPurchasePriceForApply;
        private Integer maxDiscountPrice;
        private Integer maxIssuableQuantity;
        private Integer maxUsageQuantityPerAccount;
        private Integer issuedCount;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;

        private CouponBuilder() {

        }

        public CouponBuilder issuer(Account issuer) {
            this.issuer = issuer;
            return this;
        }

        public CouponBuilder shop(Shop shop) {
            this.shop = shop;
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

        public CouponBuilder issuanceTarget(CouponIssueTarget issuanceTarget) {
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

        public CouponBuilder minimumPurchasePriceForApply(int minimumPurchasePriceForApply) {
            this.minimumPurchasePriceForApply = minimumPurchasePriceForApply;
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

        public Coupon buildGeneralCoupon(CouponUsableScope detailType) {
            return buildCoupon(CouponType.PLATFORM_COUPON, detailType);
        }

        public Coupon buildShopCoupon(boolean isApplicableSpecificProducts) {
            CouponUsableScope couponUsableScope = isApplicableSpecificProducts
                    ? CouponUsableScope.SHOP_SPECIFIC_PRODUCTS
                    : CouponUsableScope.SHOP_ALL_PRODUCT;

            return buildCoupon(CouponType.SHOP_COUPON, couponUsableScope);
        }

        private Coupon buildCoupon(CouponType type, CouponUsableScope couponUsableScope) {
            this.type = type;
            this.usageProductScope = couponUsableScope;
            this.status = CouponStatus.ISSUED;
            this.issuedCount = 0;

            validateRequiredField();
            validateLimitCondition();
            validateCouponIssuanceAuthority(issuer, type);
            validateDiscountValue(discountType, discountValue);

            return new Coupon(0L, issuer, shop, name, couponCode, type, issueQuantityLimitType,
                    couponUsableScope, status, issuanceTarget, discountType, maxIssuableQuantity,
                    maxUsageQuantityPerAccount, issuedCount, discountValue, minimumPurchasePriceForApply,
                    maxDiscountPrice, validFrom, validUntil);
        }

        private void validateRequiredField() {
            if (issuer == null || name == null || couponCode == null || type == null ||
                    usageProductScope == null || status == null || issuanceTarget == null ||
                    issuedCount == null || discountType == null || validFrom == null || validUntil == null) {
                throw CouponException.THROW_INVALID_REQUIRED_FIELD;
            }
        }

        private void validateLimitCondition() {
            if (issueQuantityLimitType.equals(FULL_LIMITED)) {
                validateMaxUsageCountPerAccount();
                validateMaxIssuableCount();
            } else if (issueQuantityLimitType.equals(MAX_ISSUABLE_LIMITED)) {
                validateMaxUsageCountPerAccount();
            } else if (issueQuantityLimitType.equals(PER_ACCOUNT_LIMITED)) {
                validateMaxIssuableCount();
            }
        }

        private void validateMaxIssuableCount() {
            if (maxIssuableQuantity <= 0) {
                throw CouponException.THROW_INVALID_REQUIRED_FIELD;
            }
        }

        private void validateMaxUsageCountPerAccount() {
            if (maxUsageQuantityPerAccount <= 0) {
                throw CouponException.THROW_INVALID_REQUIRED_FIELD;
            }
        }


        private static void validateDiscountValue(CouponDiscountType discountType, int discountValue) {
            if (discountValue <= 0 || (discountType.equals(CouponDiscountType.PERCENTAGE) && discountValue > MAX_DISCOUNT_PERCENTAGE)) {
                throw CouponException.THROW_INVALID_REQUIRED_FIELD;
            }
        }

        private static void validateCouponIssuanceAuthority(Account account, CouponType couponType) {
            switch (couponType) {
                case PLATFORM_COUPON -> {
                    if (!account.canIssueGeneralCoupon()) {
                        throw CouponException.THROW_INVALID_REQUIRED_FIELD;
                    }
                }
                case SHOP_COUPON -> {
                    if (!account.canIssueShopCoupon()) {
                        throw CouponException.THROW_INVALID_REQUIRED_FIELD;
                    }
                }
                default -> {}
            };
        }
    }

}
