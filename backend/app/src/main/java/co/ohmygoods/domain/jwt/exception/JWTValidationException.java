package co.ohmygoods.domain.jwt.exception;

public class JWTValidationException extends RuntimeException {

    public static final String TEMPLATE = "%s validation isFailed while %s, cause: %s";

    public JWTValidationException(String message) {
        super(message);
    }
}
