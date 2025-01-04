package co.ohmygoods.auth.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthError implements DomainError {

    NOT_FOUND_ACCOUNT(HttpStatus.NOT_FOUND, "A000", "Account not found"),
    NOT_FOUND_OAUTH2_AUTHORIZED_CLIENT(HttpStatus.NOT_FOUND, "A001", "OAuth2 authorized client not found"),

    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "A050", "Duplicated Account Nickname"),

    EMPTY_BEARER_HEADER(HttpStatus.UNAUTHORIZED, "A102", "insufficient authorization header"),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "A103","Expired token, please refresh token"),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "A104","Invalid token(validation failed) please re-login"),

    FAILED_OAUTH2_SIGN_OUT(HttpStatus.BAD_REQUEST, "A105", "Failed to sign out from OAuth2 provider (error code: %s, error message: %s)"),
    FAILED_OAUTH2_UNLINK(HttpStatus.BAD_REQUEST, "A106", "Failed to unlink from OAuth2 provider (error code: %s, error message: %s)");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
