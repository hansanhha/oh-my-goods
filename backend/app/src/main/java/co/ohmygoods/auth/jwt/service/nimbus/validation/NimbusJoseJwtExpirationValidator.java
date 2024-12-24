package co.ohmygoods.auth.jwt.service.nimbus.validation;

import co.ohmygoods.auth.jwt.model.vo.JWTError;
import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class NimbusJoseJwtExpirationValidator implements JwtValidator<JWT> {

    private static final Duration DEFAULT_MAX_CLOCK_SKEW = Duration.of(60, ChronoUnit.MINUTES);

    private final Duration clockSkew;
    private final Clock clock = Clock.systemUTC();

    public NimbusJoseJwtExpirationValidator() {
        clockSkew = DEFAULT_MAX_CLOCK_SKEW;
    }

    public NimbusJoseJwtExpirationValidator(Duration clockSkew) {
        this.clockSkew = clockSkew;
    }

    @Override
    public JwtValidationResult validate(JWT jwt) {
        try {
            if (Instant.now(clock).minus(clockSkew).isAfter(jwt.getJWTClaimsSet().getExpirationTime().toInstant())) {
                return JwtValidationResult.valid();
            }

            return JwtValidationResult.invalid(JWTError.EXPIRED);
        } catch (ParseException e) {
            return JwtValidationResult.invalid(JWTError.MALFORMED);
        }
    }
}
