package co.ohmygoods.auth.jwt.service.nimbus;


import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTParser;
import co.ohmygoods.auth.jwt.service.JWTValidator;
import co.ohmygoods.auth.jwt.service.dto.JWTValidationResult;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class NimbusJWTValidator implements JWTValidator {

    private static final Duration DEFAULT_MAX_CLOCK_SKEW = Duration.of(60, ChronoUnit.MINUTES);

    @Value("${application.security.jwt.issuer}")
    private String issuer;

    private final JWTParser<JWT> parser;
    private final Duration clockSkew;
    private final Clock clock = Clock.systemUTC();

    @Autowired
    public NimbusJWTValidator(JWTParser<JWT> parser) {
        this.parser = parser;
        clockSkew = DEFAULT_MAX_CLOCK_SKEW;
    }

    public NimbusJWTValidator(JWTParser<JWT> parser,Duration clockSkew) {
        this.parser = parser;
        this.clockSkew = clockSkew;
    }

    @Override
    public JWTValidationResult validate(String token) {
        var parseResult = parser.parse(token);

        return parseResult.isFailed()
                ? JWTValidationResult.parseFailure()
                : attemptValidate(parseResult.token());
    }

    private JWTValidationResult attemptValidate(JWT jwt) {
        JWTClaimsSet claimsSet = getClaimsSet(jwt);

        if (isExpired(claimsSet)) {
            return JWTValidationResult.invalid(AuthException.EXPIRED_JWT);
        }

        if (isInvalidIssuer(claimsSet)) {
            return JWTValidationResult.invalid(AuthException.INVALID_JWT);
        }

        return JWTValidationResult.valid(claimsSet.getClaims());
    }

    private boolean isExpired(JWTClaimsSet claimsSet) {
        return Instant.now(clock).minus(clockSkew).isAfter(claimsSet.getExpirationTime().toInstant());
    }

    private boolean isInvalidIssuer(JWTClaimsSet claimsSet) {
        return !claimsSet.getIssuer().equals(issuer);
    }

    private JWTClaimsSet getClaimsSet(JWT jwt) {
        try {
            return jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            throw AuthException.INVALID_JWT;
        }
    }
}
