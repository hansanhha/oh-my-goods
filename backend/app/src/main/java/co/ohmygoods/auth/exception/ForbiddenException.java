package co.ohmygoods.auth.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AuthException {

    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "Forbidden");
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
