package co.ohmygoods.auth.web.security;

import co.ohmygoods.auth.oauth2.service.IdentifiedOAuthUser;
import co.ohmygoods.auth.oauth2.service.OAuth2AttributeService;
import co.ohmygoods.auth.account.service.AccountService;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.account.service.dto.SignInResponse;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

/**
 * <p>{@link org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#successfulAuthentication}에 의해 호출됨</p>
 * <p>역할: oauth2 로그인 처리</p>
 *
 * 동작과정
 * <oi>
 *  <li>oauth2 로그인 성공 확인</li>
 *  <li>회원가입 여부에 따른 분기처리</li>
 *  <li>서비스 토큰 발급(로그인)</li>
 *  <li>리다이렉트</li>
 * </oi>
 *
 */
@Component
@Transactional
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String redirectBaseUri;
    private final RedirectStrategy redirectService;
    private final AccountService accountService;
    private final OAuth2AttributeService oAuth2AttributeService;

    /**
     * @param authentication {@link org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter}에 의해 생성된 {@link org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (!authentication.isAuthenticated()) {
            return;
        }

        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        IdentifiedOAuthUser identifiedOAuthUser = (IdentifiedOAuthUser) oauth2Token.getPrincipal();
        OAuth2Provider oAuth2Provider = OAuth2Provider.valueOf(oauth2Token.getAuthorizedClientRegistrationId().toUpperCase());
        Map<String, Object> oauth2Attributes = oauth2Token.getPrincipal().getAttributes();
        String email = oAuth2AttributeService.getEmail(oAuth2Provider, oauth2Attributes);

        if (identifiedOAuthUser.isFirstLogin()) {
            accountService.signUp(new OAuth2SignUpRequest(
                    email, oauth2Attributes, oauth2Token.getName(), identifiedOAuthUser.getMemberId(), oAuth2Provider));
        }

        SignInResponse signInResponse = accountService.signIn(identifiedOAuthUser.getMemberId());

        request.setAttribute("accessToken", signInResponse.accessToken());
        request.setAttribute("refreshToken", signInResponse.refreshToken());

        redirectService.sendRedirect(request, response, redirectBaseUri);
    }


}
