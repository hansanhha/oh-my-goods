package co.ohmygoods.auth.jwt.exception;

public class JWTValidationException extends RuntimeException {

    public static final String TEMPLATE = "%s validation failure while %s, cause: %s";

    public JWTValidationException(String message) {
        super(message);
    }
}
