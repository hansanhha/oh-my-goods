package co.ohmygoods.account.auth.oauth2;

import co.ohmygoods.account.auth.jwt.JwtService;
import co.ohmygoods.account.auth.jwt.vo.JWTs;
import co.ohmygoods.account.auth.jwt.vo.JwtClaimsKey;
import co.ohmygoods.account.info.service.AccountInfoService;
import co.ohmygoods.account.info.service.SignUpInfo;
import co.ohmygoods.account.model.OAuth2Vendor;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountInfoService accountInfoService;
    private final JwtService jwtService;
    private final RedirectStrategy redirect = new DefaultRedirectStrategy();

    /**
     * 첫 로그인인 경우 애플리케이션 계정 생성 {@link AccountInfoService} <br>
     * 애플리케이션 jwt 토큰 발급 및 리다이렉트 처리 {@link JwtService}
     * @param authentication {@link OAuth2UserPrincipal}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        var email = oAuth2UserPrincipal.getName();
        var foundId = accountInfoService.findIdByEmail(email);

        if (foundId.isEmpty()) {
            var signUpInfo = getSignUpInfo(oAuth2UserPrincipal);
            accountInfoService.signUp(signUpInfo);
        }

        var jwts = jwtService.generate(Map.of(JwtClaimsKey.SUBJECT, email));
        var redirectUri = calculateRedirectURI(request, jwts);

        redirect.sendRedirect(request, response, redirectUri.toString());
    }

    private SignUpInfo getSignUpInfo(OAuth2UserPrincipal oAuth2UserPrincipal) {
        OAuth2UserDetail oAuth2UserDetail = oAuth2UserPrincipal.getOAuth2UserDetail();
        String email = oAuth2UserPrincipal.getName();
        String oAuth2MemberId = oAuth2UserDetail.getOAuth2MemberId();
        String registrationId = oAuth2UserDetail.registrationId();
        OAuth2Vendor oAuth2Vendor = OAuth2Vendor.valueOf(registrationId.toUpperCase());

        return new SignUpInfo(email, oAuth2MemberId, oAuth2Vendor);
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
