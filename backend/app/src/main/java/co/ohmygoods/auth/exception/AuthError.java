package co.ohmygoods.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthError {

    EMPTY_BEARER_HEADER("insufficient authorization header"),
    EXPIRED_JWT("Expired token"),
    INVALID_JWT("Invalid token"),
    MALFORMED_JWT("Malformed token"),
    NOT_SIGNED_JWT("Not signed token"),
    INVALID_ISSUER_JWT("Invalid issuer");

    private final String message;
}
