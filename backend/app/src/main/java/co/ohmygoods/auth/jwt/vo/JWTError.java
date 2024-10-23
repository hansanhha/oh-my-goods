package co.ohmygoods.auth.jwt.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JWTError {

    EXPIRED("Expired token"),
    INVALID("Invalid token"),
    MALFORMED("Malformed token"),
    NOT_SIGNED("Not signed token"),
    INVALID_ISSUER("Invalid issuer");

    private final String description;
}
