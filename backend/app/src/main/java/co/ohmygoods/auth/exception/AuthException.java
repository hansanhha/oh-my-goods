package co.ohmygoods.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Instant timestamp = Instant.now();

    public AuthException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
