package co.ohmygoods.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;

@Getter
public class UnauthorizedException extends AuthenticationException {

    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private final Instant timestamp = Instant.now();

    public UnauthorizedException() {
        super(httpStatus.getReasonPhrase());
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
