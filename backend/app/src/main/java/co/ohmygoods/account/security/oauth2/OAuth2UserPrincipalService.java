package co.ohmygoods.account.security.oauth2;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2UserPrincipalService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String tokenValue = userRequest.getAccessToken().getTokenValue();

        OAuth2UserDetail oAuth2UserDetail = OAuth2UserDetail.get(registrationId, oAuth2User, tokenValue);

        return OAuth2UserPrincipal.from(oAuth2UserDetail);
    }

}
