package co.ohmygoods.auth.oauth2.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Table(name = "oAuth2AuthorizedClient")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleOAuth2AuthorizedClient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberId;

    private String clientRegistrationId;

    private String accessTokenValue;

    private String accessTokenType;

    @ElementCollection
    private Set<String> accessTokenScopes;

    private Instant accessTokenIssuedAt;

    private Instant accessTokenExpiresIn;

    private String refreshTokenValue;

    private Instant refreshTokenIssuedAt;
}
