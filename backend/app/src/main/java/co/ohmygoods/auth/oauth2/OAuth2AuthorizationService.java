package co.ohmygoods.auth.oauth2;

import org.springframework.security.core.Authentication;

public interface OAuth2AuthorizationService {

    void signOut(String subject);
    void unlink(String subject);
}
