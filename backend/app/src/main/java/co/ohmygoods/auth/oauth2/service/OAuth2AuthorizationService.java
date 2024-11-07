package co.ohmygoods.auth.oauth2.service;

import co.ohmygoods.auth.jwt.vo.JWTInfo;
import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;

public interface OAuth2AuthorizationService {

    void signOut(JWTInfo jwtInfo);
    void unlink(JWTInfo jwtInfo);
    boolean canSupport(OAuth2Vendor vendor);
}
