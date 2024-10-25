package co.ohmygoods.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;

@Getter
public class ForbiddenException extends AccessDeniedException {

    private static final HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    private final Instant timestamp = Instant.now();

    public ForbiddenException() {
        super(httpStatus.getReasonPhrase());
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
