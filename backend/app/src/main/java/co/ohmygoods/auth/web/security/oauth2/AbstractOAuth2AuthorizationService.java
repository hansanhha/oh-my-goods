package co.ohmygoods.auth.web.security.oauth2;

import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

import java.io.IOException;
import java.net.URI;

/**
 * <p>
 * 일관적인 oauth2 authorization 작업 처리
 * </p>
 *
 * 수행 작업
 * <oi>
 * <li>OAuth2AuthorizedClient 관리</li>
 * <li>oauth2 authorization 요청 전송 및 응답 매핑</li>
 * <li>템플릿 메서드를 통해 authorization 작업 후처리</li>
 * </oi>
 * @param <T> 특정 구현체의 authorization 응답 매핑 제네릭 타입
 */
@RequiredArgsConstructor
public abstract class AbstractOAuth2AuthorizationService<T> implements OAuth2AuthorizationService {

    private static final RestClient oAuth2AuthorizationRestClient;
    private static final ObjectMapper objectMapper;

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    static {
        oAuth2AuthorizationRestClient = RestClient.create();

        objectMapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public OAuth2AuthorizationResponse signOut(String email) {
        String clientRegistrationId = getClientRegistrationId();

        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, email);

        OAuth2AuthorizationResult result = sendOAuth2AuthorizationRequest(getSignOutURI(), oAuth2AuthorizedClient.getAccessToken().getTokenValue());

        OAuth2AuthorizationResponse signOutResponse = processSignOut(result.getOAuth2AuthorizationResponse(), result.getHttpStatusCode());

        if (signOutResponse.isSuccess()) {
            oAuth2AuthorizedClientService.removeAuthorizedClient(clientRegistrationId, email);
        }

        return signOutResponse;
    }

    @Override
    public OAuth2AuthorizationResponse unlink(String email) {
        String clientRegistrationId = getClientRegistrationId();

        OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, email);

        OAuth2AuthorizationResult result = sendOAuth2AuthorizationRequest(getUnlinkURI(), oAuth2AuthorizedClient.getAccessToken().getTokenValue());

        OAuth2AuthorizationResponse unlinkResponse = processUnlink(result.getOAuth2AuthorizationResponse(), result.getHttpStatusCode());

        if (unlinkResponse.isSuccess()) {
            oAuth2AuthorizedClientService.removeAuthorizedClient(clientRegistrationId, email);
        }

        return unlinkResponse;
    }

    /* ------------------------------------
                    추상 메서드
       ------------------------------------ */

    abstract protected String getClientRegistrationId();
    abstract protected URI getSignOutURI();
    abstract protected URI getUnlinkURI();

    abstract protected OAuth2AuthorizationResponse processSignOut(T response, HttpStatusCode httpStatusCode);
    abstract protected OAuth2AuthorizationResponse processUnlink(T response, HttpStatusCode httpStatusCode);

    /* ------------------------------------
                 private 메서드
       ------------------------------------ */

    private OAuth2AuthorizationResult sendOAuth2AuthorizationRequest(URI oAuthAuthorizationRequest, String oAuth2AccessToken) {
        return oAuth2AuthorizationRestClient
                .post()
                .uri(oAuthAuthorizationRequest)
                .headers(headers -> {
                    headers.setBearerAuth(oAuth2AccessToken);
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                })
                .exchange((request, response) -> {
                    HttpStatusCode statusCode = getStatusCode(response);
                    return new OAuth2AuthorizationResult(statusCode, convertToOAuth2AuthorizationResponse(response));
                });
    }

    private T convertToOAuth2AuthorizationResponse(ConvertibleClientHttpResponse response) {
        try {
            return objectMapper.readValue(response.getBody(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    private HttpStatusCode getStatusCode(ConvertibleClientHttpResponse response) {
        try {
            return response.getStatusCode();
        } catch (IOException e) {
            return null;
        }
    }

    /* ------------------------------------
         authorization 작업 결과 매핑 클래스
       ------------------------------------ */

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    protected class OAuth2AuthorizationResult {
        private HttpStatusCode httpStatusCode;
        private T oAuth2AuthorizationResponse;
    }

}
