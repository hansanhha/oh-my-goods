package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.jwt.vo.JWTInfo;
import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
@Transactional
@RequiredArgsConstructor
public class KakaoOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Value("${application.security.oauth2.client.provider.kakao.signout-uri}")
    private String signOutUri;

    @Value("${application.security.oauth2.client.provider.kakao.unlink-uri}")
    private String unlinkUri;

    @Override
    public void signOut(JWTInfo jwtInfo) {
        var oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(OAuth2Vendor.KAKAO.name().toLowerCase(), jwtInfo.subject());
        handleOAuth2RequestInternal(oAuth2AuthorizedClient.getAccessToken().getTokenValue(), signOutUri);
        oAuth2AuthorizedClientService.removeAuthorizedClient(OAuth2Vendor.KAKAO.name().toLowerCase(), jwtInfo.subject());
    }

    @Override
    public void unlink(JWTInfo jwtInfo) {
        var oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(OAuth2Vendor.KAKAO.name().toLowerCase(), jwtInfo.subject());
        handleOAuth2RequestInternal(oAuth2AuthorizedClient.getAccessToken().getTokenValue(), unlinkUri);
        oAuth2AuthorizedClientService.removeAuthorizedClient(OAuth2Vendor.KAKAO.name().toLowerCase(), jwtInfo.subject());
    }

    @Override
    public boolean canSupport(OAuth2Vendor vendor) {
        return vendor.equals(OAuth2Vendor.KAKAO);
    }

    private void handleOAuth2RequestInternal(String oAuth2AccessToken, String requestUri) {
        var restClient = buildHttpRequest(oAuth2AccessToken, requestUri);

        var response = restClient
                .post()
                .exchange(this::handleResponse);

        if (!response.isSuccessful()) {
            throw new IllegalArgumentException("kakao oauth2 연결 예외 발생: " + response.errorCode() + ", " + response.errorMessage());
        }
    }

    private RestClient buildHttpRequest(String oAuth2AccessToken, String uri) {
        return RestClient
                .builder()
                .baseUrl(uri)
                .defaultHeaders(headers -> {
                    headers.setBearerAuth(oAuth2AccessToken);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .build();
    }

    private KakaoAuthorizationResponse handleResponse(HttpRequest request, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        return KakaoAuthorizationResponse.from(response.bodyTo(KakaoAuthorizationResponseMapper.class));
    }

    private record KakaoAuthorizationResponseMapper(String id, String error, String error_description) {}

    private record KakaoAuthorizationResponse(boolean isSuccessful, String oAuth2MemberId, String errorCode, String errorMessage) {
        private static KakaoAuthorizationResponse from(KakaoAuthorizationResponseMapper mapper) {
            if (mapper.error() != null) {
                return new KakaoAuthorizationResponse(false, null, mapper.error(), mapper.error_description());
            }

            return new KakaoAuthorizationResponse(true, mapper.id(), null, null);
        }
    }

}
