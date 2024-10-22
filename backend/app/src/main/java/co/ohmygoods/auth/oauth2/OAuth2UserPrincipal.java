package co.ohmygoods.auth.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2UserPrincipal implements OAuth2User{

    private final OAuth2UserDetail oAuth2UserDetail;

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2UserDetail.attributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2UserDetail.authorities();
    }

    @Override
    public String getName() {
        return oAuth2UserDetail.email();
    }

    public OAuth2UserDetail getOAuth2UserDetail() {
        return oAuth2UserDetail;
    }

    public static OAuth2User from(OAuth2UserDetail oAuth2UserDetail) {
        return new OAuth2UserPrincipal(oAuth2UserDetail);
    }
}
