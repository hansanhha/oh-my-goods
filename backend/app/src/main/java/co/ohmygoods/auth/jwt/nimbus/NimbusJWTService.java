package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.domain.account.vo.Role;
import co.ohmygoods.auth.jwt.*;
import co.ohmygoods.domain.jwt.exception.JWTValidationException;
import co.ohmygoods.domain.jwt.entity.RefreshToken;
import co.ohmygoods.domain.oauth2.vo.OAuth2Vendor;
import co.ohmygoods.domain.jwt.vo.*;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static co.ohmygoods.domain.jwt.exception.JWTValidationException.TEMPLATE;
import static co.ohmygoods.domain.jwt.vo.JWTClaimsKey.*;
import static co.ohmygoods.domain.jwt.vo.TokenType.REFRESH_TOKEN;

@Component
@Transactional
@RequiredArgsConstructor
public class NimbusJWTService implements JWTService {

    private final JWTParser<JWT> jwtParser;
    private final JWTProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private JWTClaimValidator<JWT> jwtClaimValidator;

    @PostConstruct
    protected void init() {
        jwtClaimValidator = JWTValidators.createNimbusJWTDefaultWithIssuer(jwtProperties.getIssuer());
    }

    @Override
    public JWTs generate(Map<JWTClaimsKey, Object> claims) {
        var refreshTokenId = getRefreshTokenId();
        var accessToken = buildAccessToken(claims, getAccessTokenId(), refreshTokenId);
        var refreshToken = buildRefreshToken(claims, refreshTokenId);

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

        var claims = new HashMap<JWTClaimsKey, Object>();
        claims.put(SUBJECT,jwtInfo.subject());
        claims.put(ROLE, jwtInfo.role().name());
        claims.put(VENDOR, jwtInfo.oAuth2Vendor().name());
        var refreshTokenId = getRefreshTokenId();
        var newAccessToken = buildAccessToken(claims, getAccessTokenId(), refreshTokenId);
        var newRefreshToken = buildRefreshToken(claims, refreshTokenId);
        var refreshTokenEntity = getRefreshTokenEntity(newRefreshToken);

        refreshTokenRepository.delete(issuedRefreshToken);
        refreshTokenRepository.save(refreshTokenEntity);

        return new JWTs(newAccessToken.serialize(), newRefreshToken.serialize());
    }

    @Override
    public Optional<JWTInfo> extractTokenInfo(String token) {
        var parseResult = jwtParser.parse(token);
        if (parseResult.isFailed()) {
            return Optional.empty();
        }

        var jwt = parseResult.token();
        var optionalJwtClaimsSet = getClaimsSet(jwt);
        Optional<JWTInfo> jwtInfo = Optional.empty();

        if (optionalJwtClaimsSet.isPresent()) {
            jwtInfo = Optional.of(buildJWTInfo(optionalJwtClaimsSet.get(), token));
        }

        return jwtInfo;
    }

    @Override
    public void revokeRefreshToken(String accessToken) {
        var parseResult = jwtParser.parse(accessToken);
        if (parseResult.isFailed()) {
            return;
        }

        var jwt = parseResult.token();
        var optionalJWTClaimsSet = getClaimsSet(jwt);
        if (optionalJWTClaimsSet.isEmpty()) {
            return;
        }

        var claimsSet = optionalJWTClaimsSet.get();
        var referenceRefreshTokenId = (String) claimsSet.getClaim(JWTClaimsKey.REFERENCE_REFRESH_TOKEN_ID.name());
        if (referenceRefreshTokenId == null) {
            return;
        }

        var optionalSavedRefreshToken = refreshTokenRepository.findByJwtId(referenceRefreshTokenId);

        optionalSavedRefreshToken.ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public JWTValidationResult validateToken(String token) {
        var parseResult = jwtParser.parse(token);
        if (parseResult.isFailed()) {
            return JWTValidationResult.error(parseResult.cause());
        }

        var jwt = parseResult.token();
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
        var jwtInfo = buildJWTInfo(claimsSet, token);

        return JWTValidationResult.valid(jwtInfo);
    }

    private Optional<JWTClaimsSet> getClaimsSet(JWT jwt) {
        try {
            return Optional.of(jwt.getJWTClaimsSet());
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    private SignedJWT buildAccessToken(Map<JWTClaimsKey, Object> claims, String accessTokenId, String refreshTokenId) {
        var jwsHeader = new JWSHeader(jwtProperties.getAlgorithm());
        var accessTokenClaimsSet = buildAccessTokenClaimsSet(claims, accessTokenId, refreshTokenId);
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

    private SignedJWT buildRefreshToken(Map<JWTClaimsKey, Object> claims, String refreshTokenId) {
        var jwsHeader = new JWSHeader(jwtProperties.getAlgorithm());
        var refreshTokenClaimsSet = buildRefreshTokenClaimsSet(claims, refreshTokenId);
        var refreshToken = new SignedJWT(jwsHeader, refreshTokenClaimsSet);

        try {
            var accessTokenSigner = new MACSigner(jwtProperties.getRefreshTokenKey());
            refreshToken.sign(accessTokenSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate refresh token", e);
        }

        return refreshToken;
    }

    private JWTClaimsSet buildAccessTokenClaimsSet(Map<JWTClaimsKey, Object> claims, String accessTokenId, String refreshTokenId) {
        var accessTokenClaims = new HashMap<>(claims);
        accessTokenClaims.put(JWTClaimsKey.REFERENCE_REFRESH_TOKEN_ID, refreshTokenId);
        return buildClaimsSet(accessTokenClaims, jwtProperties.getAccessTokenExpiresIn(), accessTokenId);
    }

    private JWTClaimsSet buildRefreshTokenClaimsSet(Map<JWTClaimsKey, Object> claims, String refreshTokenId) {
        return buildClaimsSet(claims, jwtProperties.getRefreshTokenExpiresIn(), refreshTokenId);
    }

    private JWTClaimsSet buildClaimsSet(Map<JWTClaimsKey, ?> claims, Duration expiresIn, String refreshTokenId) {
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
                .jwtID(refreshTokenId);

        return builder.build();
    }

    private RefreshToken getRefreshTokenEntity(SignedJWT refreshToken) {
        var optionalRefreshTokenClaimsSet = getClaimsSet(refreshToken);

        if (optionalRefreshTokenClaimsSet.isEmpty()) {
            throw new RuntimeException("Unable build refresh token entity");
        }

        var refreshTokenClaimsSet = optionalRefreshTokenClaimsSet.get();
        var serializedRefreshTokenValue = refreshToken.serialize();

        return RefreshToken.builder()
                .tokenValue(serializedRefreshTokenValue)
                .jwtId(refreshTokenClaimsSet.getJWTID())
                .subject(refreshTokenClaimsSet.getSubject())
                .issuer(refreshTokenClaimsSet.getIssuer())
                .audience(refreshTokenClaimsSet.getAudience().getFirst())
                .issuedAt(LocalDateTime.ofInstant(refreshTokenClaimsSet.getIssueTime().toInstant(), ZoneId.systemDefault()))
                .expiresIn(LocalDateTime.ofInstant(refreshTokenClaimsSet.getExpirationTime().toInstant(), ZoneId.systemDefault()))
                .build();
    }

    private JWTInfo buildJWTInfo(JWTClaimsSet jwtClaimsSet, String token) {
        return JWTInfo
                .builder()
                .tokenValue(token)
                .subject(jwtClaimsSet.getSubject())
                .oAuth2Vendor(OAuth2Vendor.valueOf(jwtClaimsSet.getClaim(VENDOR.name()).toString().toUpperCase()))
                .role(Role.valueOf(jwtClaimsSet.getClaim(ROLE.name()).toString().toUpperCase()))
                .issuer(jwtClaimsSet.getIssuer())
                .audience(jwtClaimsSet.getAudience().getFirst())
                .issuedAt(jwtClaimsSet.getIssueTime().toInstant())
                .expiresIn(jwtClaimsSet.getExpirationTime().toInstant())
                .build();
    }
}
