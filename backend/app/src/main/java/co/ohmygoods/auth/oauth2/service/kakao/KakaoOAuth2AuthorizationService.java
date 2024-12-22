package co.ohmygoods.auth.oauth2.service.kakao;

import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import co.ohmygoods.auth.oauth2.service.AbstractOAuth2AuthorizationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

import static co.ohmygoods.auth.oauth2.service.kakao.KakaoOAuth2AuthorizationService.KakaoOAuth2AuthorizationResponse;

@Component
@Transactional
public class KakaoOAuth2AuthorizationService
        extends AbstractOAuth2AuthorizationService<KakaoOAuth2AuthorizationResponse> {

    @Value("${application.security.oauth2.client.provider.kakao.signout-uri}")
    private String signOutUri;

    @Value("${application.security.oauth2.client.provider.kakao.unlink-uri}")
    private String unlinkUri;

    public KakaoOAuth2AuthorizationService(OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        super(oAuth2AuthorizedClientService);
    }

    @Override
    protected OAuth2AuthorizationResponse processSignOut(KakaoOAuth2AuthorizationResponse response, HttpStatusCode httpStatusCode) {
        return response.isError()
                ? OAuth2AuthorizationResponse.fail(httpStatusCode, response.code(), response.msg())
                : OAuth2AuthorizationResponse.success(httpStatusCode);
    }

    @Override
    protected OAuth2AuthorizationResponse processUnlink(KakaoOAuth2AuthorizationResponse response, HttpStatusCode httpStatusCode) {
        return response.isError()
                ? OAuth2AuthorizationResponse.fail(httpStatusCode, response.code(), response.msg())
                : OAuth2AuthorizationResponse.success(httpStatusCode);
    }

    @Override
    protected String getClientRegistrationId() {
        return OAuth2Provider.KAKAO.name().toLowerCase();
    }

    @Override
    protected URI getSignOutURI() {
        return URI.create(signOutUri);
    }

    @Override
    protected URI getUnlinkURI() {
        return URI.create(unlinkUri);
    }

    @Override
    public boolean canSupport(OAuth2Provider vendor) {
        return vendor.equals(OAuth2Provider.KAKAO);
    }

    protected record KakaoOAuth2AuthorizationResponse(String id,
                                                      String code,
                                                      String msg) {

        public boolean isError() {
            // 카카오 로그인 예외 코드는 모두 음수로 표시됨
            // https://developers.kakao.com/docs/latest/ko/rest-api/reference#response-format
            return code != null && Integer.parseInt(code) < 0;
        }
    }

}
