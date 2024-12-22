package co.ohmygoods.auth.jwt.exception;

public class JWTValidationException extends RuntimeException {

    public static final String TEMPLATE = "%s validation isFailed while %s, error: %s";

    public JWTValidationException(String message) {
        super(message);
    }
}
