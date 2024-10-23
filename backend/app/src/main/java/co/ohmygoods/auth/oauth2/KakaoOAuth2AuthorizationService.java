package co.ohmygoods.auth.oauth2;

import co.ohmygoods.auth.account.SignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final SignService signService;

    @Value("${application.security.oauth2.client.provider.kakao.signout-uri}")
    private String signOutUri;

    @Value("${application.security.oauth2.client.provider.kakao.unlink-uri}")
    private String unlinkUri;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        signService.signOut(authentication.getName());
        handleOAuth2RequestInternal((OAuth2UserPrincipal) authentication, signOutUri);
    }

    @Override
    public void unlink(OAuth2UserPrincipal oAuth2UserPrincipal) {
        signService.deleteAccount(oAuth2UserPrincipal.getName());
        handleOAuth2RequestInternal(oAuth2UserPrincipal, unlinkUri);
    }

    private void handleOAuth2RequestInternal(OAuth2UserPrincipal oAuth2UserPrincipal, String requestUri) {
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

    private record SuccessResponse(String id) {
    }

    private record FailureResponse(String error, String error_description) {
    }

    private record KakaoAuthResponse(boolean isSuccessful, String errorCode, String errorMessage) {
        private static KakaoAuthResponse from(SuccessResponse response) {
            return new KakaoAuthResponse(true, null, null);
        }

        private static KakaoAuthResponse from(FailureResponse response) {
            return new KakaoAuthResponse(false, response.error(), response.error_description());
        }
    }

}
