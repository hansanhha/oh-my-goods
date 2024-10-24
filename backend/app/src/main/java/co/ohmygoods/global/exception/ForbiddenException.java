package co.ohmygoods.global.exception;

import lombok.Getter;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;

@Getter
public class ForbiddenException extends AccessDeniedException {

    private final Instant timestamp = Instant.now();

    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
