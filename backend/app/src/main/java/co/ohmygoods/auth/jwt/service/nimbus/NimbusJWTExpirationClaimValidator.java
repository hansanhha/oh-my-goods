package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.jwt.service.JWTClaimValidator;
import co.ohmygoods.auth.jwt.model.vo.JWTError;
import co.ohmygoods.auth.jwt.model.vo.JWTValidationResult;
import com.nimbusds.jwt.JWT;

import java.text.ParseException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class NimbusJWTExpirationClaimValidator implements JWTClaimValidator<JWT> {

    private static final Duration DEFAULT_MAX_CLOCK_SKEW = Duration.of(60, ChronoUnit.MINUTES);

    private final Duration clockSkew;
    private final Clock clock = Clock.systemUTC();

    public NimbusJWTExpirationClaimValidator() {
        clockSkew = DEFAULT_MAX_CLOCK_SKEW;
    }

    public NimbusJWTExpirationClaimValidator(Duration clockSkew) {
        this.clockSkew = clockSkew;
    }

    @Override
    public JWTValidationResult validate(JWT jwt) {
        try {
            if (Instant.now(clock).minus(clockSkew).isAfter(jwt.getJWTClaimsSet().getExpirationTime().toInstant())) {
                return JWTValidationResult.success();
            }

            return JWTValidationResult.error(JWTError.EXPIRED);
        } catch (ParseException e) {
            return JWTValidationResult.error(JWTError.MALFORMED);
        }
    }
}
