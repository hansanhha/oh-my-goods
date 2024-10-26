package co.ohmygoods.auth.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

public interface OAuth2AuthorizationService extends LogoutHandler {

    void unlink(Authentication authentication);
}
