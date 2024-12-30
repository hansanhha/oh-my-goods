package co.ohmygoods.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final AuthError error;

    public JwtAuthenticationException(AuthError error) {
        super(error.getMessage());
        this.error = error;
    }
}
