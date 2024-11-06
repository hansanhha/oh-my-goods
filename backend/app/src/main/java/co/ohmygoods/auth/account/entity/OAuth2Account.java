package co.ohmygoods.auth.account.entity;

import co.ohmygoods.auth.account.vo.Role;
import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Account extends BaseEntity {

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
    private OAuth2Vendor oauth2Vendor;

    private String oauth2MemberId;
}

