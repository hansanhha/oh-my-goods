package co.ohmygoods.auth.oauth2.service;


import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;


@Component
@Transactional
@RequiredArgsConstructor
public class KakaoOAuth2APIService implements OAuth2APIService {

    private static final OAuth2Provider KAKAO = OAuth2Provider.KAKAO;

    private final RestClient kakaoOAuth2APIClient;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Value("${application.security.oauth2.client.provider.kakao.signout-uri}")
    private String signOutUri;

    @Value("${application.security.oauth2.client.provider.kakao.unlink-uri}")
    private String unlinkUri;

    @Override
    public void signOut(String email) {
        findCachedOAuth2AuthorizedClient(email)
        .ifPresent(cachedClient -> { 
            sendKakaoOAuth2APIRequest(cachedClient, signOutUri);
        });
    }

    @Override
    public void unlink(String email) {
        findCachedOAuth2AuthorizedClient(email)
        .ifPresent(cachedClient -> { 
            sendKakaoOAuth2APIRequest(cachedClient, unlinkUri);
        });
    }

    @Override
    public boolean isSupport(OAuth2Provider provider) {
        return provider.equals(KAKAO);
    }

    private Optional<OAuth2AuthorizedClient> findCachedOAuth2AuthorizedClient(String email) {
        return Optional.ofNullable(oAuth2AuthorizedClientService.loadAuthorizedClient(KAKAO.toString().toLowerCase(), email));
    }

    private void sendKakaoOAuth2APIRequest(OAuth2AuthorizedClient oAuth2AuthorizedClient, String requestUri) {
        kakaoOAuth2APIClient
            .post()
            .uri(unlinkUri)
            .headers(headers -> {
                headers.setBearerAuth(oAuth2AuthorizedClient.getAccessToken().getTokenValue());
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            })
            .retrieve()
            .toBodilessEntity();
    }

}
