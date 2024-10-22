package co.ohmygoods.auth.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@RequiredArgsConstructor
public class KakaoOAuth2AuthorizationService implements OAuth2AuthorizationService {

    @Value("${application.security.oauth2.client.provider.kakao.signout-uri}")
    private String signOutUri;

    @Value("${application.security.oauth2.client.provider.kakao.unlink-uri}")
    private String unlinkUri;

    @Override
    public void signOut(OAuth2UserPrincipal oAuth2UserPrincipal) {
        handleOAuth2Internal(oAuth2UserPrincipal, signOutUri);
    }

    @Override
    public void unlink(OAuth2UserPrincipal oAuth2UserPrincipal) {
        handleOAuth2Internal(oAuth2UserPrincipal, unlinkUri);
    }

    private void handleOAuth2Internal(OAuth2UserPrincipal oAuth2UserPrincipal, String requestUri) {
        var accessToken = oAuth2UserPrincipal.getOAuth2UserDetail().oauth2AccessTokenValue();

        var restClient = buildHttpRequest(accessToken, requestUri);

        var response = restClient
                .post()
                .exchange(this::handleResponse);

        if (!response.isSuccessful()) {
            throw new IllegalArgumentException("kakao oauth2 연결 예외 발생: " + response.errorCode() + ", " + response.errorMessage());
        }
    }

    private RestClient buildHttpRequest(String accessToken, String uri) {
        return RestClient
                .builder()
                .baseUrl(uri)
                .defaultHeaders(headers -> {
                    headers.setBearerAuth(accessToken);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .build();
    }

    private KakaoAuthResponse handleResponse(HttpRequest request, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is2xxSuccessful()) {
            return KakaoAuthResponse.from(response.bodyTo(SuccessResponse.class));
        } else {
            return KakaoAuthResponse.from(response.bodyTo(FailureResponse.class));
        }
    }

    private record SuccessResponse(String id) {}
    private record FailureResponse(String error, String error_description) {}

    private record KakaoAuthResponse(boolean isSuccessful, String errorCode, String errorMessage) {
        private static KakaoAuthResponse from(SuccessResponse response) {
            return new KakaoAuthResponse(true, null, null);
        }

        private static KakaoAuthResponse from(FailureResponse response) {
            return new KakaoAuthResponse(false, response.error(), response.error_description());
        }
    }

}
