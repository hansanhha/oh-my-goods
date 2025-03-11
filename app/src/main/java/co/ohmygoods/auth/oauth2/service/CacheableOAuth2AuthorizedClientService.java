package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.oauth2.model.entity.SimpleOAuth2AuthorizedClient;
import co.ohmygoods.auth.oauth2.repository.SimpleOAuth2AuthorizedClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CacheableOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {

    private static final String REDIS_CACHE_NAMES = "oauth2AuthorizedClient";

    private final SimpleOAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;


    /**
     * @param memberId {@link IdentifiedOAuth2UserService}에서 최초 로그인 시 부여하는 값으로 서비스 애플리케이션에서
     *                 end-user를 식별하기 위한 목적으로 사용됨
     */
    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(cacheNames = REDIS_CACHE_NAMES, key = "#memberId")
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String memberId) {
        SimpleOAuth2AuthorizedClient simpleOAuth2AuthorizedClient = oAuth2AuthorizedClientRepository
                .findByMemberId(memberId).orElseThrow(AuthException::notFoundOAuth2AuthorizedClient);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        return (T) new OAuth2AuthorizedClient(clientRegistration, memberId,
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                        simpleOAuth2AuthorizedClient.getAccessTokenValue(),
                        simpleOAuth2AuthorizedClient.getAccessTokenIssuedAt(),
                        simpleOAuth2AuthorizedClient.getAccessTokenExpiresIn()));
    }

    /**
     * @param principal end-user, principal.getName() 동작은 서비스 member id 를 반환하는 {@link IdentifiedOAuthUser}에 의해 오버라이딩되며
     *                 member id는 {@link IdentifiedOAuth2UserService}에서 최초 로그인 시 부여하는 값으로 서비스 애플리케이션에서
     *                 end-user를 식별하기 위한 목적으로 사용됨
     */
    @Override
    @CachePut(cacheNames = REDIS_CACHE_NAMES, key = "#principal.name")
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

        SimpleOAuth2AuthorizedClient simpleOAuth2AuthorizedClient = SimpleOAuth2AuthorizedClient.builder()
                .memberId(principal.getName())
                .clientRegistrationId(authorizedClient.getClientRegistration().getRegistrationId())
                .accessTokenType(accessToken.getTokenType().getValue())
                .accessTokenValue(accessToken.getTokenValue())
                .accessTokenScopes(accessToken.getScopes())
                .accessTokenIssuedAt(accessToken.getIssuedAt())
                .accessTokenExpiresIn(accessToken.getExpiresAt())
                .refreshTokenValue(refreshToken.getTokenValue())
                .refreshTokenIssuedAt(refreshToken.getIssuedAt())
                .build();

        oAuth2AuthorizedClientRepository.save(simpleOAuth2AuthorizedClient);
    }

    /**
     * @param memberId {@link IdentifiedOAuth2UserService}에서 최초 로그인 시 부여하는 값으로 서비스 애플리케이션에서
     *                 end-user를 식별하기 위한 목적으로 사용됨
     */
    @Override
    @CacheEvict(cacheNames = REDIS_CACHE_NAMES, key = "memberId")
    public void removeAuthorizedClient(String clientRegistrationId, String memberId) {
        oAuth2AuthorizedClientRepository.deleteByMemberId(memberId);
    }
}
