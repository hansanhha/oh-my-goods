package co.ohmygoods.auth.oauth2;

import co.ohmygoods.domain.jwt.vo.JWTInfo;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class DelegatingOAuth2AuthorizationService implements OAuth2AuthorizationService {

    private final List<OAuth2AuthorizationService> oAuth2AuthorizationServices;

    @Override
    public void signOut(JWTInfo jwtInfo) {
        oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(jwtInfo.oAuth2Vendor()))
                .findFirst()
                .ifPresent(service -> service.signOut(jwtInfo));
    }

    @Override
    public void unlink(JWTInfo jwtInfo) {
        oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport(jwtInfo.oAuth2Vendor()))
                .findFirst()
                .ifPresent(service -> service.unlink(jwtInfo));
    }

    @Override
    public boolean canSupport(OAuth2Vendor vendor) {
        return vendor.equals(OAuth2Vendor.KAKAO) || vendor.equals(OAuth2Vendor.NAVER);
    }
}
