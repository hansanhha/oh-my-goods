package co.ohmygoods.auth.oauth2.service;


import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;

import java.util.Map;

import org.springframework.stereotype.Component;


@Component
public class OAuth2UserAttributeUtils {

    public String getUniqueOAuth2MemberId(OAuth2Provider oAuth2Provider, String originalOAuth2MemberId) {
        return oAuth2Provider.name().toLowerCase().concat("_").concat(originalOAuth2MemberId);
    }

    @SuppressWarnings("unchecked")
    public String extractUserEmail(OAuth2Provider oAuth2Provider, Map<String, Object> attributes) {
        return switch (oAuth2Provider) {
            case KAKAO -> (String) ((Map<String, Object>)attributes.get("kakao_account")).get("email");
            case NAVER -> null;
        };
    }
}
