package co.ohmygoods.account.domain;

import co.ohmygoods.global.jpa.BaseEntity;
import jakarta.persistence.*;

@Entity
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

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private int businessConversionCount = 0;

    @Enumerated(EnumType.STRING)
    private OAuth2Vendor oauth2Vendor;

    private String oauth2MemberId;
}