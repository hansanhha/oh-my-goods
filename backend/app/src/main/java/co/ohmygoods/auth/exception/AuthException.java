package co.ohmygoods.auth.exception;

import co.ohmygoods.global.exception.DomainError;
import co.ohmygoods.global.exception.DomainException;
import org.springframework.http.HttpStatus;

public class AuthException extends DomainException {

    public static final AuthException NOT_FOUND_ACCOUNT = new AuthException(AuthError.NOT_FOUND_ACCOUNT);
    public static final AuthException DUPLICATED_NICKNAME = new AuthException(AuthError.DUPLICATED_NICKNAME);

    public static final AuthException EMPTY_BEARER_HEADER = new AuthException(AuthError.EMPTY_BEARER_HEADER);
    public static final AuthException INVALID_JWT = new AuthException(AuthError.INVALID_JWT);
    public static final AuthException EXPIRED_JWT = new AuthException(AuthError.EXPIRED_JWT);

    private static final AuthException FAILED_OAUTH2_SIGN_OUT = new AuthException(AuthError.FAILED_OAUTH2_SIGN_OUT);
    private static final AuthException FAILED_OAUTH2_UNLINK = new AuthException(AuthError.FAILED_OAUTH2_UNLINK);

    public AuthException(AuthError error) {
        super(error);
    }

    private AuthException(DomainError domainError) {
        super(domainError);
    }

    public static AuthException notFoundAccount() {
        return NOT_FOUND_ACCOUNT;
    }

    public static AuthException failedOAuth2SignOut(String oauth2ErrorCode, String oauth2ErrorMsg) {
        String formatted = String.format(FAILED_OAUTH2_SIGN_OUT.getErrorMessage(), oauth2ErrorCode, oauth2ErrorMsg);

        return new AuthException(new DomainError() {

            @Override
            public HttpStatus getHttpStatus() {
                return FAILED_OAUTH2_SIGN_OUT.getHttpStatus();
            }

            @Override
            public String getErrorCode() {
                return FAILED_OAUTH2_SIGN_OUT.getErrorCode();
            }

            @Override
            public String getErrorMessage() {
                return formatted;
            }
        });
    }

    public static AuthException failedOAuth2Unlink(String oauth2ErrorCode, String oauth2ErrorMsg) {
        String formatted = String.format(FAILED_OAUTH2_UNLINK.getErrorMessage(), oauth2ErrorCode, oauth2ErrorMsg);

        return new AuthException(new DomainError() {

            @Override
            public HttpStatus getHttpStatus() {
                return FAILED_OAUTH2_UNLINK.getHttpStatus();
            }

            @Override
            public String getErrorCode() {
                return FAILED_OAUTH2_UNLINK.getErrorCode();
            }

            @Override
            public String getErrorMessage() {
                return formatted;
            }
        });
    }

    public static AuthException of(AuthError error) {
        return switch (error) {
            case AuthError.NOT_FOUND_ACCOUNT -> NOT_FOUND_ACCOUNT;
            case AuthError.DUPLICATED_NICKNAME -> DUPLICATED_NICKNAME;
            case AuthError.EMPTY_BEARER_HEADER -> EMPTY_BEARER_HEADER;
            case AuthError.INVALID_JWT -> INVALID_JWT;
            case AuthError.EXPIRED_JWT -> EXPIRED_JWT;
            case AuthError.FAILED_OAUTH2_SIGN_OUT -> FAILED_OAUTH2_SIGN_OUT;
            case AuthError.FAILED_OAUTH2_UNLINK -> FAILED_OAUTH2_UNLINK;
        };
    }
}
