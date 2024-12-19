package co.ohmygoods.auth.web.security.oauth2;

import co.ohmygoods.auth.jwt.model.vo.JWTInfo;

public interface OAuth2AuthorizationService {

    void signOut(JWTInfo jwtInfo);
    void unlink(JWTInfo jwtInfo);
    boolean canSupport(OAuth2Vendor vendor);

    enum OAuth2Vendor {
        KAKAO,
        NAVER;
    }
}
