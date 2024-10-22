package co.ohmygoods.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record OAuth2UserDetail(String registrationId,
                               String email,
                               String oauth2AccessTokenValue,
                               Map<String, Object> attributes,
                               Collection<? extends GrantedAuthority> authorities) {

    private static final String KAKAO = "kakao";


    public static OAuth2UserDetail get(String registrationId, OAuth2User oAuth2User, String tokenValue) {
        return switch (registrationId) {
            case KAKAO -> new OAuth2UserDetail(registrationId,
                    extractKakaoAccountEmail(oAuth2User),
                    tokenValue,
                    oAuth2User.getAttributes(),
                    oAuth2User.getAuthorities());
            default -> throw new IllegalArgumentException("Unsupported oauth2 vendor");
        };
    }

    public String getOAuth2MemberId() {
        return switch (registrationId) {
            case KAKAO -> extractKakaoMemberId();
            default -> throw new IllegalArgumentException("Unsupported oauth2 vendor");
        };
    }

    private String extractKakaoMemberId() {
        return attributes.get("id").toString();
    }

    @SuppressWarnings("unchecked")
    private static String extractKakaoAccountEmail(OAuth2User oAuth2User) {
        return ((Map<String, Object>)oAuth2User.getAttributes().get("kakao_account")).get("email").toString();
    }

}
