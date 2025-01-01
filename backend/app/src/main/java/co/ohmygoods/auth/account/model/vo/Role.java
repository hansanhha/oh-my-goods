package co.ohmygoods.auth.account.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static co.ohmygoods.auth.account.model.vo.Role.Authority.*;


@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", Set.of(
            PURCHASE_PRODUCT
    )),

    BUSINESS("ROLE_BUSINESS", Set.of(
            MANAGE_PRODUCT,
            MANAGE_CONTENT,
            ISSUE_SHOP_COUPON,
            MANAGE_SALES
    )),

    ADMIN("ROLE_ADMIN", Set.of(
            MANAGE_ACTIVITY_STATUS_USERS,
            MANAGE_ACTIVITY_STATUS_BUSINESS,
            MANAGE_TOTAL_SALES,
            ISSUE_SHOP_COUPON,
            ISSUE_GENERAL_COUPON
    ));

    private final String roleName;
    private final Set<Authority> authorities;

    public boolean hasIssueGeneralCouponAuthority() {
        return this.getAuthorities().contains(ISSUE_GENERAL_COUPON);
    }

    public boolean hasIssueShopCouponAuthority() {
        return this.getAuthorities().contains(ISSUE_SHOP_COUPON);
    }

    public boolean hasDestroyShopCouponAuthority() {
        return this.getAuthorities().contains(DESTROY_SHOP_COUPON);
    }

    public boolean hasDestroyGeneralCouponAuthority() {
        return this.getAuthorities().contains(DESTROY_GENERAL_COUPON);
    }

    public Collection<? extends GrantedAuthority> toGrantedAuthorities() {
        return this.authorities.stream()
                .map(authority -> (GrantedAuthority) authority::name)
                .toList();
    }

    @Getter
    public enum Authority {
        PURCHASE_PRODUCT,

        MANAGE_PRODUCT,
        MANAGE_CONTENT,
        MANAGE_SALES,

        ISSUE_GENERAL_COUPON,
        ISSUE_SHOP_COUPON,

        DESTROY_SHOP_COUPON,
        DESTROY_GENERAL_COUPON,

        MANAGE_ACTIVITY_STATUS_USERS,
        MANAGE_ACTIVITY_STATUS_BUSINESS,
        MANAGE_TOTAL_SALES;

    }
}
