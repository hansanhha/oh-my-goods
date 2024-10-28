package co.ohmygoods.auth.oauth2;

import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;

public interface OAuth2AuthorizationService {

    void signOut(String subject);
    void unlink(String subject);
    boolean canSupport(OAuth2Vendor vendor);
}
