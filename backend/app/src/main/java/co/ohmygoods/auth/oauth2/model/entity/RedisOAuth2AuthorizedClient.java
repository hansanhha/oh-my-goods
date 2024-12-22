package co.ohmygoods.auth.oauth2.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

import java.time.Instant;
import java.util.Set;

@Entity
@RedisHash("OAuth2AuthorizedClient")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisOAuth2AuthorizedClient {

    @Id
    private String email;

    private String clientRegistrationId;

    private String accessTokenType;

    private String accessTokenValue;

    private Instant accessTokenIssuedAt;

    private Instant accessTokenExpiresAt;

    private Set<String> accessTokenScopes;

    private String refreshTokenValue;

    private Instant refreshTokenIssuedAt;

    public static RedisOAuth2AuthorizedClient createBySpringSecurityOAuth2(OAuth2AuthorizedClient client, Authentication authentication) {
        ClientRegistration clientRegistration = client.getClientRegistration();
        OAuth2AccessToken accessToken = client.getAccessToken();
        OAuth2RefreshToken refreshToken = client.getRefreshToken();

        return new RedisOAuth2AuthorizedClient(authentication.getName(),
                clientRegistration.getClientId(), accessToken.getTokenType().getValue(),
                accessToken.getTokenValue(), accessToken.getIssuedAt(), accessToken.getExpiresAt(),
                accessToken.getScopes(), refreshToken.getTokenValue(), refreshToken.getIssuedAt());
    }
}