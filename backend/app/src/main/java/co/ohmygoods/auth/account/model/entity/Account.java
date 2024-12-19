package co.ohmygoods.auth.account.model.entity;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.web.security.oauth2.OAuth2AuthorizationService;
import co.ohmygoods.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String nickname;

    private String profileImageName;

    private String profileImagePath;

    private String phone;

    @Column(nullable = false)
    private int businessConversionCount = 0;

    @Enumerated(EnumType.STRING)
    private OAuth2AuthorizationService.OAuth2Vendor oauth2Vendor;

    private String oauth2MemberId;

    public boolean canIssueGeneralCoupon() {
        return role.hasIssueGeneralCouponAuthority();
    }

    public boolean canIssueShopCoupon() {
        return role.hasIssueShopCouponAuthority();
    }

    public boolean canDestroyShopCoupon() {
        return role.hasDestroyShopCouponAuthority();
    }
}

