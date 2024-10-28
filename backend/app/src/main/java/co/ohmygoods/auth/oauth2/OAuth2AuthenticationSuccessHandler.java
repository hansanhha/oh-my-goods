package co.ohmygoods.auth.oauth2;

import co.ohmygoods.auth.account.OAuth2SignService;
import co.ohmygoods.auth.account.dto.OAuth2AccountDTO;
import co.ohmygoods.auth.account.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.jwt.JWTService;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;
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

    private final OAuth2SignService oAuth2SignService;
    private final RedirectStrategy redirect = new DefaultRedirectStrategy();

    /**
     * 첫 OAuth2 로그인인 경우 애플리케이션 계정 생성 {@link OAuth2SignService} <br>
     * 서비스 로그인(애플리케이션 jwt 토큰 발급 및 리다이렉트 처리) {@link JWTService}
     * @param authentication {@link OAuth2UserPrincipal}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        var oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        var email = oAuth2UserPrincipal.getName();
        var optionalOAuth2AccountDTO = oAuth2SignService.getOne(email);

        OAuth2AccountDTO oAuth2AccountDTO;
        if (optionalOAuth2AccountDTO.isEmpty()) {
            var signUpInfo = buildSignUpInfo(oAuth2UserPrincipal);
            oAuth2AccountDTO = oAuth2SignService.signUp(signUpInfo);
        } else {
            oAuth2AccountDTO = optionalOAuth2AccountDTO.get();
        }

        var jwts = oAuth2SignService.signIn(email, OAuth2Vendor.valueOf(oAuth2UserPrincipal.getOAuth2UserDetail().registrationId().toUpperCase()), oAuth2AccountDTO.role());
        var redirectUri = calculateRedirectURI(request, jwts);

        redirect.sendRedirect(request, response, redirectUri.toString());
    }

    private OAuth2SignUpRequest buildSignUpInfo(OAuth2UserPrincipal oAuth2UserPrincipal) {
        OAuth2UserDetail oAuth2UserDetail = oAuth2UserPrincipal.getOAuth2UserDetail();
        String email = oAuth2UserPrincipal.getName();
        String oAuth2MemberId = oAuth2UserDetail.getOAuth2MemberId();
        String registrationId = oAuth2UserDetail.registrationId();
        OAuth2Vendor oAuth2Vendor = OAuth2Vendor.valueOf(registrationId.toUpperCase());

        return new OAuth2SignUpRequest(email, oAuth2MemberId, oAuth2Vendor);
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
