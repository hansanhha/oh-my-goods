package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.*;
import co.ohmygoods.auth.jwt.exception.JWTValidationException;
import co.ohmygoods.auth.jwt.model.RefreshToken;
import co.ohmygoods.auth.jwt.vo.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
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
import java.util.Optional;
import java.util.UUID;

import static co.ohmygoods.auth.jwt.exception.JWTValidationException.TEMPLATE;
import static co.ohmygoods.auth.jwt.vo.JWTClaimsKey.ROLE;
import static co.ohmygoods.auth.jwt.vo.JWTClaimsKey.SUBJECT;
import static co.ohmygoods.auth.jwt.vo.TokenType.REFRESH_TOKEN;

@Component
@RequiredArgsConstructor
public class NimbusJWTService implements JWTService {

    private final JWTParseService<JWT> jwtParseService;
    private final JWTProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private JWTClaimValidator<JWT> jwtClaimValidator;

    @PostConstruct
    protected void init() {
        jwtClaimValidator = JWTValidators.createNimbusJWTDefaultWithIssuer(jwtProperties.getIssuer());
    }

    @Override
    public JWTs generate(Map<JWTClaimsKey, Object> claims) {
        var accessToken = buildAccessToken(claims, getAccessTokenId());
        var refreshToken = buildRefreshToken(claims, getRefreshTokenId());

        var refreshTokenEntity = getRefreshTokenEntity(refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new JWTs(accessToken.serialize(), refreshToken.serialize());
    }

    @Override
    public JWTs regenerate(String refreshToken) {
        var validationResult = validateToken(refreshToken);

        if (validationResult.hasError()) {
            throw new JWTValidationException(TEMPLATE.formatted(REFRESH_TOKEN, "regenerating token", validationResult.error().getDescription()));
        }

        var jwtInfo = validationResult.jwtInfo();
        var optionalRefreshToken = refreshTokenRepository.findByJwtId(jwtInfo.jwtId());

        if (optionalRefreshToken.isEmpty()) {
            // 해당 토큰 subject에게 발급된 모든 refresh token을 삭제할 수도 있음
            throw new JWTValidationException(TEMPLATE.formatted(REFRESH_TOKEN, "regenerating token", "not found token that matches with jti"));
        }

        var issuedRefreshToken = optionalRefreshToken.get();

        if (!refreshToken.equals(issuedRefreshToken.getTokenValue())) {
            throw new JWTValidationException(TEMPLATE.formatted(REFRESH_TOKEN, "regenerating token", "not equals signed token value of received and datasource"));
        }

        var claims = Map.of(SUBJECT, jwtInfo.subject(), ROLE, jwtInfo.role());
        var newAccessToken = buildAccessToken(claims, getAccessTokenId());
        var newRefreshToken = buildRefreshToken(claims, getRefreshTokenId());
        var refreshTokenEntity = getRefreshTokenEntity(newRefreshToken);

        refreshTokenRepository.delete(issuedRefreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new JWTs(newAccessToken.serialize(), newRefreshToken.serialize());
    }

    @Override
    public void revokeRefreshToken(String accessToken) {
        var parsedJWT = jwtParseService.parse(accessToken);
        if (parsedJWT.isFailed()) {
            return;
        }

        var jwt = parsedJWT.token();
        var optionalJWTClaimsSet = getClaimsSet(jwt);
        if (optionalJWTClaimsSet.isEmpty()) {
            return;
        }

        var claimsSet = optionalJWTClaimsSet.get();
        var savedRefreshTokens = refreshTokenRepository.findAllBySubject(claimsSet.getSubject());

        if (!savedRefreshTokens.isEmpty()) {
            refreshTokenRepository.deleteAll(savedRefreshTokens);
        }
    }

    @Override
    public JWTValidationResult validateToken(String token) {
        var paredJWT = jwtParseService.parse(token);
        if (paredJWT.isFailed()) {
            return JWTValidationResult.error(paredJWT.cause());
        }

        var jwt = paredJWT.token();
        if (!(jwt instanceof SignedJWT)) {
            return JWTValidationResult.error(JWTError.NOT_SIGNED);
        }

        var result = jwtClaimValidator.validate(jwt);
        if (result.hasError()) {
            return result;
        }

        var optionalClaimsSet = getClaimsSet(jwt);
        if (optionalClaimsSet.isEmpty()) {
            return JWTValidationResult.error(JWTError.INVALID);
        }

        var claimsSet = optionalClaimsSet.get();
        var jwtInfo = JWTInfo
                .builder()
                .subject(claimsSet.getSubject())
                .role((String) claimsSet.getClaim(ROLE.name()))
                .issuer(claimsSet.getIssuer())
                .audience(claimsSet.getAudience().getFirst())
                .issuedAt(claimsSet.getIssueTime().toInstant())
                .expiresIn(claimsSet.getExpirationTime().toInstant())
                .build();

        return JWTValidationResult.valid(jwtInfo);
    }

    private Optional<JWTClaimsSet> getClaimsSet(JWT jwt) {
        try {
            return Optional.of(jwt.getJWTClaimsSet());
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    private SignedJWT buildAccessToken(Map<JWTClaimsKey, ?> claims, String jwtId) {
        var jwsHeader = new JWSHeader(jwtProperties.getAlgorithm());
        var accessTokenClaimsSet = buildAccessTokenClaimsSet(claims, jwtId);
        var accessToken = new SignedJWT(jwsHeader, accessTokenClaimsSet);

        try {
            var accessTokenSigner = new MACSigner(jwtProperties.getAccessTokenKey());
            accessToken.sign(accessTokenSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate access token", e);
        }

        return accessToken;
    }

    private String getAccessTokenId() {
        return UUID.randomUUID().toString();
    }

    private String getRefreshTokenId() {
        Optional<RefreshToken> alreadyExistRefreshToken;
        String refreshTokenId;

        do {
            refreshTokenId = UUID.randomUUID().toString();
            alreadyExistRefreshToken = refreshTokenRepository.findByJwtId(refreshTokenId);
        } while (alreadyExistRefreshToken.isPresent());

        return refreshTokenId;
    }

    private SignedJWT buildRefreshToken(Map<JWTClaimsKey, ?> claims, String jwtId) {
        var jwsHeader = new JWSHeader(jwtProperties.getAlgorithm());
        var refreshTokenClaimsSet = buildRefreshTokenClaimsSet(claims, jwtId);
        var refreshToken = new SignedJWT(jwsHeader, refreshTokenClaimsSet);

        try {
            var accessTokenSigner = new MACSigner(jwtProperties.getRefreshTokenKey());
            refreshToken.sign(accessTokenSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate refresh token", e);
        }

        return refreshToken;
    }

    private JWTClaimsSet buildAccessTokenClaimsSet(Map<JWTClaimsKey, ?> claims, String jwtId) {
        return buildClaimsSet(claims, jwtProperties.getAccessTokenExpiresIn(), jwtId);
    }

    private JWTClaimsSet buildRefreshTokenClaimsSet(Map<JWTClaimsKey, ?> claims, String jwtId) {
        return buildClaimsSet(claims, jwtProperties.getRefreshTokenExpiresIn(), jwtId);
    }

    private JWTClaimsSet buildClaimsSet(Map<JWTClaimsKey, ?> claims, Duration expiresIn, String jwtId) {
        var issuer = jwtProperties.getIssuer();
        var issuedAt = Instant.now();
        var expirationTime = issuedAt.plus(expiresIn);
        var audience = jwtProperties.getAudience();

        var builder = new JWTClaimsSet.Builder()
                .issuer(issuer)
                .issueTime(Date.from(issuedAt))
                .audience(audience)
                .expirationTime(Date.from(expirationTime))
                .subject((String) claims.get(SUBJECT))
                .jwtID(jwtId);

        claims.forEach((key, value) -> builder.claim(key.name(), value));

        return builder.build();
    }

    private RefreshToken getRefreshTokenEntity(SignedJWT refreshToken) {
        try {
            var refreshTokenClaimsSet = refreshToken.getJWTClaimsSet();
            var serializedRefreshTokenValue = refreshToken.serialize();

            return RefreshToken.builder()
                    .tokenValue(serializedRefreshTokenValue)
                    .jwtId(refreshTokenClaimsSet.getJWTID())
                    .subject(refreshTokenClaimsSet.getSubject())
                    .issuer(refreshTokenClaimsSet.getIssuer())
                    .audience(refreshTokenClaimsSet.getAudience().getFirst())
                    .issuedAt(LocalDateTime.from(refreshTokenClaimsSet.getIssueTime().toInstant()))
                    .expiresIn(LocalDateTime.from(refreshTokenClaimsSet.getExpirationTime().toInstant()))
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException("Unable build refresh token entity");
        }
    }
}
