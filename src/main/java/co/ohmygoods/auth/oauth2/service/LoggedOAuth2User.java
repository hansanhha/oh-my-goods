package co.ohmygoods.auth.oauth2.service;


import co.ohmygoods.auth.account.service.SignInService;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;

import lombok.Getter;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


/**
 * 
 * DefaultOAuth2User를 상속하고 getName 메서드의 반환 값을 memberId(서비스에서 부여한 OAuth2 사용자 식별자 아이디 값)로 한다
 * 
 * 
 * <oi>
 *  <li>{@link OAuth2UserLoginService}</li>
 *  <li>{@link OAuth2AuthenticationSuccessHandler#onAuthenticationSuccess}</li>
 *  <li>{@link SignInService#signUp}</li>
 * </oi>
 */
@Getter
public class LoggedOAuth2User extends DefaultOAuth2User {

    private final String memberId;
    private final boolean firstLogin;

    public LoggedOAuth2User(DefaultOAuth2User defaultOAuth2User, String memberId, boolean firstLogin, String usernameAttributeName) {
        super(defaultOAuth2User.getAuthorities(), defaultOAuth2User.getAttributes(), usernameAttributeName);
        this.memberId = memberId;
        this.firstLogin = firstLogin;
    }

    @Override
    public String getName() {
        return memberId;
    }

}
