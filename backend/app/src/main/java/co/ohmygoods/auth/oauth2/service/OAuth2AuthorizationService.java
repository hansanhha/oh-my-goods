package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.account.service.dto.OAuth2AuthorizationResponse;
import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;

public interface OAuth2AuthorizationService {

    OAuth2AuthorizationResponse signOut(String email);
    OAuth2AuthorizationResponse unlink(String email);
    boolean canSupport(OAuth2Provider oAuth2Provider);

}
