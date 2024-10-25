package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTService;
import co.ohmygoods.auth.jwt.JWTValidator;
import co.ohmygoods.auth.jwt.JWTValidators;
import co.ohmygoods.auth.jwt.RefreshTokenRepository;
import co.ohmygoods.auth.jwt.model.RefreshToken;
import co.ohmygoods.auth.jwt.vo.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NimbusJWTService implements JWTService {

    private final JWTProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private JWTValidator<JWT> jwtValidator;

    @PostConstruct
    protected void init() {
        jwtValidator = JWTValidators.createNimbusJWTDefaultWithIssuer(jwtProperties.getIssuer());
    }

    @Override
    public JWTs generate(Map<JWTClaimsKey, Object> claims) {
        var savedRefreshTokens = refreshTokenRepository.findAllBySubject((String) claims.get(JWTClaimsKey.SUBJECT));

        if (!savedRefreshTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(savedRefreshTokens);
        }

        var accessTokenKey = jwtProperties.getAccessTokenKey();
        var refreshTokenKey = jwtProperties.getRefreshTokenKey();
        var algorithm = jwtProperties.getAlgorithm();

        var jwsHeader = new JWSHeader(algorithm);
        var accessTokenClaimsSet = buildAccessTokenClaimsSet(claims);
        var refreshTokenClaimsSet = buildRefreshTokenClaimsSet(claims);

        var nimbusAccessToken = new SignedJWT(jwsHeader, accessTokenClaimsSet);
        var nimbusRefreshToken = new SignedJWT(jwsHeader, refreshTokenClaimsSet);

        try {
            var accessTokenSigner = new MACSigner(accessTokenKey);
            var refreshTokenSigner = new MACSigner(refreshTokenKey);
            nimbusAccessToken.sign(accessTokenSigner);
            nimbusRefreshToken.sign(refreshTokenSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate JWT", e);
        }

        var serializedRefreshTokenValue = nimbusRefreshToken.serialize();
        var refreshToken = RefreshToken.builder()
                .tokenValue(serializedRefreshTokenValue)
                .jwtId(refreshTokenClaimsSet.getJWTID())
                .subject(refreshTokenClaimsSet.getSubject())
                .issuer(refreshTokenClaimsSet.getIssuer())
                .audience(refreshTokenClaimsSet.getAudience().getFirst())
                .issuedAt(LocalDateTime.from(refreshTokenClaimsSet.getIssueTime().toInstant()))
                .expiresIn(LocalDateTime.from(refreshTokenClaimsSet.getExpirationTime().toInstant()))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new JWTs(nimbusAccessToken.serialize(), serializedRefreshTokenValue);
    }

    @Override
    public JWTs regenerate(String refreshToken) {
        return null;
    }

    @Override
    public void deleteAllByEmail(String email) {
        var savedRefreshTokens = refreshTokenRepository.findAllBySubject(email);

        if (!savedRefreshTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(savedRefreshTokens);
        }
    }

    @Override
    public JwtValidationResult validateToken(String token) {
        try {
            var jwt = JWTParser.parse(token);

            if (!(jwt instanceof SignedJWT)) {
                return JwtValidationResult.error(JWTError.NOT_SIGNED);
            }

            var result = jwtValidator.validate(jwt);

            if (result.hasError()) {
                return result;
            }

            var claimsSet = jwt.getJWTClaimsSet();
            var jwtInfo = JWTInfo
                    .builder()
                    .subject(claimsSet.getSubject())
                    .role((String) claimsSet.getClaim(JWTClaimsKey.ROLE.name()))
                    .issuer(claimsSet.getIssuer())
                    .audience(claimsSet.getAudience().getFirst())
                    .issuedAt(claimsSet.getIssueTime().toInstant())
                    .expiresIn(claimsSet.getExpirationTime().toInstant())
                    .build();

            return JwtValidationResult.valid(jwtInfo);

        } catch (Exception ex) {
            if (ex instanceof ParseException) {
                return JwtValidationResult.error(JWTError.MALFORMED);
            }
            return JwtValidationResult.error(JWTError.INVALID);
        }
    }

    private JWTClaimsSet buildAccessTokenClaimsSet(Map<JWTClaimsKey, Object> claims) {
        return buildClaimsSet(claims, jwtProperties.getAccessTokenExpiresIn());
    }

    private JWTClaimsSet buildRefreshTokenClaimsSet(Map<JWTClaimsKey, Object> claims) {
        return buildClaimsSet(claims, jwtProperties.getRefreshTokenExpiresIn());
    }

    private JWTClaimsSet buildClaimsSet(Map<JWTClaimsKey, Object> claims, Duration expiresIn) {
        var jwtId = UUID.randomUUID().toString();
        var issuer = jwtProperties.getIssuer();
        var issuedAt = Instant.now();
        var expirationTime = issuedAt.plus(expiresIn);
        var audience = jwtProperties.getAudience();

        var builder = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(Date.from(issuedAt))
                .audience(audience)
                .expirationTime(Date.from(expirationTime))
                .subject((String) claims.get(JWTClaimsKey.SUBJECT))
                .jwtID(jwtId);

        claims.forEach((key, value) -> builder.claim(key.name(), value));

        return builder.build();
    }
}
