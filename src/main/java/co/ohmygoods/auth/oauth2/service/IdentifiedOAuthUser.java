package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.account.service.SignInService;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;
import lombok.Getter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

/**
 *
 * oauth2 end-user 사용자를 식별하기 위한 서비스의 member id를 부여한 DefaultOAuth2User 객체
 * <p>getName 메서드 오버라이딩: 서비스 member id 반환</p>
 *
 *
 * 로그인 및 회원가입 시 identifiedMemberId 사용
 * <oi>
 *  <li>{@link IdentifiedOAuth2UserService}</li>
 *  <li>{@link OAuth2AuthenticationSuccessHandler#onAuthenticationSuccess}</li>
 *  <li>{@link SignInService#signUp}</li>
 *  <li>{@link CacheableOAuth2AuthorizedClientService}</li>
 * </oi>
 */
@Getter
public class IdentifiedOAuthUser extends DefaultOAuth2User {

    private final String memberId;
    private final boolean firstLogin;

    public IdentifiedOAuthUser(DefaultOAuth2User defaultOAuth2User, String memberId, boolean firstLogin, String usernameAttributeName) {
        super(defaultOAuth2User.getAuthorities(), defaultOAuth2User.getAttributes(), usernameAttributeName);
        this.memberId = memberId;
        this.firstLogin = firstLogin;
    }

    @Override
    public String getName() {
        return memberId;
    }
}
