package co.ohmygoods.account.auth.oauth2;

import co.ohmygoods.account.model.OAuth2Vendor;
import co.ohmygoods.account.info.service.AccountInfoService;
import co.ohmygoods.account.info.service.SignUpInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountInfoService accountInfoService;
    private final RedirectStrategy redirect = new DefaultRedirectStrategy();

    /**
     * 첫 로그인인 경우 애플리케이션 계정 생성
     * 애플리케이션 jwt 토큰 발급 및 리다이렉트 처리
     * @param authentication {@link OAuth2UserPrincipal}
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Long id;
        var oAuth2UserPrincipal = (OAuth2UserPrincipal) authentication.getPrincipal();
        var email = oAuth2UserPrincipal.getName();
        var foundId = accountInfoService.findIdByEmail(email);

        if (foundId.isPresent()) {
            id = foundId.get();
        } else {
            var signUpInfo = getSignUpInfo(oAuth2UserPrincipal);
            id = accountInfoService.signUp(signUpInfo);
        }





    }

    private SignUpInfo getSignUpInfo(OAuth2UserPrincipal oAuth2UserPrincipal) {
        OAuth2UserDetail oAuth2UserDetail = oAuth2UserPrincipal.getOAuth2UserDetail();
        String email = oAuth2UserPrincipal.getName();
        String oAuth2MemberId = oAuth2UserDetail.getOAuth2MemberId();
        String registrationId = oAuth2UserDetail.registrationId();
        OAuth2Vendor oAuth2Vendor = OAuth2Vendor.valueOf(registrationId.toUpperCase());

        return new SignUpInfo(email, oAuth2MemberId, oAuth2Vendor);
    }

    private URI calculateRedirectURI(HttpServletRequest request, ) {

    }


}
