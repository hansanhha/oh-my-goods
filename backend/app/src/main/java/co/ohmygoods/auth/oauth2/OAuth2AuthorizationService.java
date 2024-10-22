package co.ohmygoods.auth.oauth2;

public interface OAuth2AuthorizationService {

    void signOut(OAuth2UserPrincipal oAuth2UserPrincipal);
    void unlink(OAuth2UserPrincipal oAuth2UserPrincipal);
}
