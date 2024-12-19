package co.ohmygoods.auth.web.security.oauth2;

import co.ohmygoods.auth.account.service.AccountService;
import co.ohmygoods.auth.account.service.SignService;
import co.ohmygoods.auth.account.service.dto.AccountResponse;
import co.ohmygoods.auth.account.service.dto.OAuth2SignUpRequest;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.jwt.model.vo.JWTs;
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

/*
    OAuth2 로그인 과정
    1. OAuth2AuthorizationRequestRedirectFilter -> 로그인 페이지 리다이렉트
    2. OAuth2AuthorizationRequestRedirectFilter ->
 */
@Component
@Transactional
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SignService signService;
    private final AccountService accountService;
    private final RedirectStrategy redirect = new DefaultRedirectStrategy();

    /**
     * 첫 OAuth2 로그인인 경우 애플리케이션 계정 생성 {@link SignService} <br>
     * 서비스 로그인(애플리케이션 jwt 토큰 발급 및 리다이렉트 처리) {@link JWTService}
     * @param authentication {@link OAuth2UserPrincipal}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        var oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        var email = oAuth2UserPrincipal.getName();
        var optionalOAuth2AccountDTO = accountService.getOne(email);

        AccountResponse accountResponse;
        if (optionalOAuth2AccountDTO.isEmpty()) {
            var signUpInfo = buildSignUpInfo(oAuth2UserPrincipal);
            accountResponse = signService.signUp(signUpInfo);
        } else {
            accountResponse = optionalOAuth2AccountDTO.get();
        }

        var jwts = signService.signIn(email, OAuth2AuthorizationService.OAuth2Vendor.valueOf(oAuth2UserPrincipal.getOAuth2UserDetail().registrationId().toUpperCase()), accountResponse.role());
        var redirectUri = calculateRedirectURI(request, jwts);

        redirect.sendRedirect(request, response, redirectUri.toString());
    }

    private OAuth2SignUpRequest buildSignUpInfo(OAuth2UserPrincipal oAuth2UserPrincipal) {
        OAuth2UserDetail oAuth2UserDetail = oAuth2UserPrincipal.getOAuth2UserDetail();
        String email = oAuth2UserPrincipal.getName();
        String oAuth2MemberId = oAuth2UserDetail.getOAuth2MemberId();
        String registrationId = oAuth2UserDetail.registrationId();
        OAuth2AuthorizationService.OAuth2Vendor oAuth2Vendor = OAuth2AuthorizationService.OAuth2Vendor.valueOf(registrationId.toUpperCase());

        return new OAuth2SignUpRequest(email, oAuth2MemberId, oAuth2Vendor);
    }

    private URI calculateRedirectURI(HttpServletRequest request, JWTs jwts) {
        return UriComponentsBuilder.fromUriString("/")
                .queryParam("access_token", jwts.accessToken())
                .queryParam("refresh_token", jwts.refreshToken())
                .build()
                .toUri();
    }


}
