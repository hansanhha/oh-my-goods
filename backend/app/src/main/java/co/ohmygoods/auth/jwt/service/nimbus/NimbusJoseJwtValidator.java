package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.exception.AuthError;
import co.ohmygoods.auth.jwt.service.JwtParser;
import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
public class NimbusJoseJwtValidator implements JwtValidator {

    private static final Duration DEFAULT_MAX_CLOCK_SKEW = Duration.of(60, ChronoUnit.MINUTES);

    @Value("${application.security.jwt.issuer}")
    private String issuer;

    private final JwtParser<JWT> parser;
    private final Duration clockSkew;
    private final Clock clock = Clock.systemUTC();

    @Autowired
    public NimbusJoseJwtValidator(@Qualifier("nimbusJoseJwtParser") JwtParser<JWT> parser) {
        this.parser = parser;
        clockSkew = DEFAULT_MAX_CLOCK_SKEW;
    }

    public NimbusJoseJwtValidator(@Qualifier("nimbusJoseJwtParser") JwtParser<JWT> parser,
                                  Duration clockSkew) {
        this.parser = parser;
        this.clockSkew = clockSkew;
    }

    @Override
    public JwtValidationResult validate(String token) {
        var parseResult = parser.parse(token);

        return parseResult.isFailed()
                ? JwtValidationResult.parseFailure()
                : attemptValidate(parseResult.token());
    }

    private JwtValidationResult attemptValidate(JWT jwt) {
        if (isExpired(jwt)) {
            return JwtValidationResult.invalid(AuthError.EXPIRED_JWT);
        }

        if (isInvalidIssuer(jwt)) {
            return JwtValidationResult.invalid(AuthError.INVALID_JWT);
        }

        return JwtValidationResult.valid(getClaimsSet(jwt)
                .map(JWTClaimsSet::getClaims)
                .orElse(null));
    }

    private boolean isExpired(JWT jwt) {
        return getClaimsSet(jwt)
                .map(claims ->
                        Instant.now(clock).minus(clockSkew).isAfter(claims.getExpirationTime().toInstant()))
                .orElse(false);
    }

    private boolean isInvalidIssuer(JWT jwt) {
        return getClaimsSet(jwt)
                .map(claims -> !Objects.equals(claims.getIssuer(), issuer))
                .orElse(false);
    }

    private Optional<JWTClaimsSet> getClaimsSet(JWT jwt) {
        try {
            return Optional.of(jwt.getJWTClaimsSet());
        } catch (ParseException e) {
            return Optional.empty();
        }
    }
}
