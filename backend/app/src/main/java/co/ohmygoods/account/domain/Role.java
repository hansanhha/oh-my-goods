package co.ohmygoods.account.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;


@Getter
public enum Role {

    USER("ROLE_USER", Set.of(
            Authority.PURCHASE_PRODUCT
    )),

    BUSINESS("ROLE_BUSINESS", Set.of(
            Authority.MANAGE_PRODUCT,
            Authority.MANAGE_CONTENT,
            Authority.MANAGE_SALES
    )),

    ADMIN("ROLE_ADMIN", Set.of(
            Authority.MANAGE_ACTIVITY_STATUS_USERS,
            Authority.MANAGE_ACTIVITY_STATUS_BUSINESS,
            Authority.MANAGE_TOTAL_SALES
    ));

    private final String roleName;
    private final Set<Authority> authorities;

    Role(String roleName, Set<Authority> authorities) {
        this.roleName = roleName;
        this.authorities = authorities;
    }

    @Getter
    public enum Authority {
        PURCHASE_PRODUCT,

        MANAGE_PRODUCT,
        MANAGE_CONTENT,
        MANAGE_SALES,

        MANAGE_ACTIVITY_STATUS_USERS,
        MANAGE_ACTIVITY_STATUS_BUSINESS,
        MANAGE_TOTAL_SALES;

    }
}
