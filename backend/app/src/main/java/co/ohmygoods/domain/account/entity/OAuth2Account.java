package co.ohmygoods.domain.account.entity;

import co.ohmygoods.domain.account.vo.Role;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;
import co.ohmygoods.domain.global.BaseEntity;
import co.ohmygoods.domain.shop.entity.Shop;
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

    @OneToOne(mappedBy = "owner")
    private Shop shop;

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

