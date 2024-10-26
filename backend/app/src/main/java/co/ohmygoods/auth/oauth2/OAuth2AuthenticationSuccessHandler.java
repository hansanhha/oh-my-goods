package co.ohmygoods.auth.oauth2;

import co.ohmygoods.auth.jwt.JWTService;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.account.SignService;
import co.ohmygoods.auth.account.SignUpRequest;
import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Component
@Transactional
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SignService signService;
    private final RedirectStrategy redirect = new DefaultRedirectStrategy();

    /**
     * 첫 OAuth2 로그인인 경우 애플리케이션 계정 생성 {@link SignService} <br>
     * 서비스 로그인(애플리케이션 jwt 토큰 발급 및 리다이렉트 처리) {@link JWTService}
     * @param authentication {@link OAuth2UserPrincipal}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        var email = oAuth2UserPrincipal.getName();
        var foundId = signService.findIdByEmail(email);

        if (foundId.isEmpty()) {
            var signUpInfo = getSignUpInfo(oAuth2UserPrincipal);
            signService.signUp(signUpInfo);
        }

        var jwts = signService.signIn(email);
        var redirectUri = calculateRedirectURI(request, jwts);

        redirect.sendRedirect(request, response, redirectUri.toString());
    }

    private SignUpRequest getSignUpInfo(OAuth2UserPrincipal oAuth2UserPrincipal) {
        OAuth2UserDetail oAuth2UserDetail = oAuth2UserPrincipal.getOAuth2UserDetail();
        String email = oAuth2UserPrincipal.getName();
        String oAuth2MemberId = oAuth2UserDetail.getOAuth2MemberId();
        String registrationId = oAuth2UserDetail.registrationId();
        OAuth2Vendor oAuth2Vendor = OAuth2Vendor.valueOf(registrationId.toUpperCase());

        return new SignUpRequest(email, oAuth2MemberId, oAuth2Vendor);
    }

    private URI calculateRedirectURI(HttpServletRequest request, JWTs jwts) {
        String redirectUri = request.getParameter("redirect_uri");
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("access_token", jwts.accessToken())
                .queryParam("refresh_token", jwts.refreshToken())
                .build()
                .toUri();
    }


}
