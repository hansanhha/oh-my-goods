package co.ohmygoods.auth.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AuthException {

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
