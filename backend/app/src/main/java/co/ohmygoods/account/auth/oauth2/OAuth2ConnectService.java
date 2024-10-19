package co.ohmygoods.account.auth.oauth2;

public interface OAuth2ConnectService<T extends OAuth2UserPrincipal> {

    void signOut(T t);
    void unlink(T t);
}
