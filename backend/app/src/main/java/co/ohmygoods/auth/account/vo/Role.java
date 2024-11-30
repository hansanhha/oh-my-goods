package co.ohmygoods.auth.account.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;


@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", Set.of(
            Authority.PURCHASE_PRODUCT
    )),

    BUSINESS("ROLE_BUSINESS", Set.of(
            Authority.MANAGE_PRODUCT,
            Authority.MANAGE_CONTENT,
            Authority.ISSUE_SHOP_COUPON,
            Authority.MANAGE_SALES
    )),

    ADMIN("ROLE_ADMIN", Set.of(
            Authority.MANAGE_ACTIVITY_STATUS_USERS,
            Authority.MANAGE_ACTIVITY_STATUS_BUSINESS,
            Authority.MANAGE_TOTAL_SALES,
            Authority.ISSUE_SHOP_COUPON,
            Authority.ISSUE_GENERAL_COUPON
    ));

    private final String roleName;
    private final Set<Authority> authorities;

    public boolean hasIssueGeneralCouponAuthority() {
        return this.getAuthorities().contains(Authority.ISSUE_GENERAL_COUPON);
    }

    public boolean hasIssueShopCouponAuthority() {
        return this.getAuthorities().contains(Authority.ISSUE_SHOP_COUPON);
    }

    @Getter
    public enum Authority {
        PURCHASE_PRODUCT,

        MANAGE_PRODUCT,
        MANAGE_CONTENT,
        MANAGE_SALES,

        ISSUE_GENERAL_COUPON,
        ISSUE_SHOP_COUPON,

        MANAGE_ACTIVITY_STATUS_USERS,
        MANAGE_ACTIVITY_STATUS_BUSINESS,
        MANAGE_TOTAL_SALES;

    }
}
