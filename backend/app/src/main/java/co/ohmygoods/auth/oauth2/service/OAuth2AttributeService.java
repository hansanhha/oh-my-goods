package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OAuth2AttributeService {

    public String getCombinedOAuth2MemberId(OAuth2Provider oAuth2Provider, String originalOAuth2MemberId) {
        return oAuth2Provider.name().toLowerCase().concat("_").concat(originalOAuth2MemberId);
    }

    @SuppressWarnings("unchecked")
    public String getEmail(OAuth2Provider oAuth2Provider, Map<String, Object> attributes) {
        return switch (oAuth2Provider) {
            case KAKAO -> (String) ((Map<String, Object>)attributes.get("kakao_account")).get("email");
            case NAVER -> null;
        };
    }
}
