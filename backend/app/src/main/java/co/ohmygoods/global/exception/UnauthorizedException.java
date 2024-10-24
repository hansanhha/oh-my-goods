package co.ohmygoods.global.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

import java.time.Instant;

@Getter
public class UnauthorizedException extends AuthenticationException {

    private final Instant timestamp = Instant.now();

    public UnauthorizedException() {
        super("Unauthorized");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
