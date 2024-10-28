package co.ohmygoods.auth.oauth2;

import co.ohmygoods.auth.oauth2.vo.OAuth2Vendor;
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
    public void signOut(String subject) {
        oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport())
                .findFirst()
                .ifPresent(service -> service.signOut(subject));
    }

    @Override
    public void unlink(String subject) {
        oAuth2AuthorizationServices.stream()
                .filter(service -> service.canSupport())
                .findFirst()
                .ifPresent(service -> service.unlink(subject));
    }

    @Override
    public boolean canSupport(OAuth2Vendor vendor) {
        return vendor.equals(OAuth2Vendor.KAKAO) || vendor.equals(OAuth2Vendor.NAVER);
    }
}
