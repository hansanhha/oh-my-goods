package co.ohmygoods.auth.oauth2.service;


import co.ohmygoods.auth.oauth2.model.vo.OAuth2Provider;


public interface OAuth2APIService {

    void signOut(String email);

    void unlink(String email);

    boolean isSupport(OAuth2Provider oAuth2Provider);

}
