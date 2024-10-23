package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.model.RefreshToken;
import co.ohmygoods.auth.jwt.vo.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NimbusJwtService implements JwtService {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public JWTs generate(Map<JwtClaimsKey, Object> claims) {
        var savedRefreshTokens = refreshTokenRepository.findAllBySubject((String) claims.get(JwtClaimsKey.SUBJECT));

        if (!savedRefreshTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(savedRefreshTokens);
        }

        var key = jwtProperties.getKey();
        var algorithm = jwtProperties.getAlgorithm();

        var jwsHeader = new JWSHeader(algorithm);
        var accessTokenClaimsSet = buildAccessTokenClaimsSet(claims);
        var refreshTokenClaimsSet = buildRefreshTokenClaimsSet(claims);

        var nimbusAccessToken = new SignedJWT(jwsHeader, accessTokenClaimsSet);
        var nimbusRefreshToken = new SignedJWT(jwsHeader, refreshTokenClaimsSet);

        try {
            var signer = new MACSigner(key);
            nimbusAccessToken.sign(signer);
            nimbusRefreshToken.sign(signer);
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
        return null;
    }

    private JWTClaimsSet buildAccessTokenClaimsSet(Map<JwtClaimsKey, Object> claims) {
        return buildClaimsSet(claims, jwtProperties.getAccessTokenExpiresIn());
    }

    private JWTClaimsSet buildRefreshTokenClaimsSet(Map<JwtClaimsKey, Object> claims) {
        return buildClaimsSet(claims, jwtProperties.getRefreshTokenExpiresIn());
    }

    private JWTClaimsSet buildClaimsSet(Map<JwtClaimsKey, Object> claims, Duration expiresIn) {
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
                .subject((String) claims.get(JwtClaimsKey.SUBJECT))
                .jwtID(jwtId);

        claims.forEach((key, value) -> builder.claim(key.name(), value));

        return builder.build();
    }
}
