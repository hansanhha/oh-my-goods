package co.ohmygoods.auth.oauth2;

import co.ohmygoods.domain.jwt.vo.JWTInfo;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;

public interface OAuth2AuthorizationService {

    void signOut(JWTInfo jwtInfo);
    void unlink(JWTInfo jwtInfo);
    boolean canSupport(OAuth2Vendor vendor);
}
