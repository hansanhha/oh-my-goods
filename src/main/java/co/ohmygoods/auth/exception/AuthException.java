package co.ohmygoods.auth.exception;


import co.ohmygoods.global.exception.DomainError;
import co.ohmygoods.global.exception.DomainException;


public class AuthException extends DomainException {

    public static final AuthException NOT_FOUND_ACCOUNT = new AuthException(AuthError.NOT_FOUND_ACCOUNT);
    public static final AuthException NOT_FOUND_OAUTH2_AUTHORIZED_CLIENT = new AuthException(AuthError.NOT_FOUND_OAUTH2_AUTHORIZED_CLIENT);

    public static final AuthException INVALID_UPDATE_NICKNAME_SIZE = new AuthException(AuthError.INVALID_UPDATE_NICKNAME_SIZE);
    public static final AuthException DUPLICATED_NICKNAME = new AuthException(AuthError.DUPLICATED_NICKNAME);

    public static final AuthException EMPTY_BEARER_HEADER = new AuthException(AuthError.EMPTY_BEARER_HEADER);
    public static final AuthException INVALID_JWT = new AuthException(AuthError.INVALID_JWT);
    public static final AuthException EXPIRED_JWT = new AuthException(AuthError.EXPIRED_JWT);
    public static final AuthException STOLEN_JWT = new AuthException(AuthError.STOLEN_JWT);

    public static final AuthException FAILED_SIGN_NIMBUS_JWT = new AuthException(AuthError.FAILED_SIGN_NIMBUS_JWT);

    public AuthException(AuthError error) {
        super(error);
    }

    private AuthException(DomainError domainError) {
        super(domainError);
    }

    public static AuthException notFoundAccount() {
        return NOT_FOUND_ACCOUNT;
    }

    public static AuthException notFoundOAuth2AuthorizedClient() {
        return NOT_FOUND_OAUTH2_AUTHORIZED_CLIENT;
    }
}
