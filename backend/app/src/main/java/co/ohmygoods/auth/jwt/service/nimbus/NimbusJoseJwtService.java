package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.jwt.config.JWTProperties;
import co.ohmygoods.auth.jwt.model.vo.JwtProvider;
import co.ohmygoods.auth.jwt.model.vo.TokenType;
import co.ohmygoods.auth.jwt.repository.RefreshTokenRepository;
import co.ohmygoods.auth.jwt.service.AbstractJwtService;
import co.ohmygoods.auth.jwt.service.CacheableRefreshTokenService;
import co.ohmygoods.auth.jwt.service.JWTParser;
import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.ValidationResult;
import co.ohmygoods.auth.jwt.service.dto.TokenDTO;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class NimbusJoseJwtService extends AbstractJwtService {

    private final JWTParser<JWT> parser;
    private final JwtValidator<JWT> validator;
    private final JWTProperties jwtProperties;

    public NimbusJoseJwtService(AccountRepository accountRepository,
                                CacheableRefreshTokenService refreshTokenService,
                                JWTParser<JWT> parser,
                                JWTProperties jwtProperties,
                                @Qualifier("nimbusJoseJwtValidatorDelegate") JwtValidator<JWT> validator) {
        super(accountRepository, refreshTokenService);
        this.parser = parser;
        this.jwtProperties = jwtProperties;
        this.validator = validator;
    }

    @Override
    protected TokenDTO generateAccessToken(String email, Set<Role.Authority> scopes) {
        Instant issueTime = Instant.now();
        Instant expirationTime = issueTime.plus(jwtProperties.getAccessTokenExpiresIn());

        JWSHeader jwsHeader = getJWSHeader(jwtProperties.getAccessTokenAlgorithm());
        JWTClaimsSet accessTokenClaimsSet = getClaimsSet(email, getJwtId(), Date.from(issueTime), Date.from(expirationTime), scopes);
        SignedJWT signedAccessToken = getSignedNimbusJwt(jwsHeader, accessTokenClaimsSet, jwtProperties.getAccessTokenKey());

        return new TokenDTO(signedAccessToken.serialize(), TokenType.BEARER, jwtProperties.getAccessTokenExpiresIn());
    }

    @Override
    protected TokenDTO generateRefreshToken(String email) {
        Instant issueTime = Instant.now();
        Instant expirationTime = issueTime.plus(jwtProperties.getRefreshTokenExpiresIn());

        JWSHeader jwsHeader = getJWSHeader(jwtProperties.getRefreshTokenAlgorithm());
        JWTClaimsSet refreshTokenClaimsSet = getClaimsSet(email, getJwtId(), Date.from(issueTime), Date.from(expirationTime), null);
        SignedJWT signedRefreshToken = getSignedNimbusJwt(jwsHeader, refreshTokenClaimsSet, jwtProperties.getRefreshTokenKey());

        return new TokenDTO(signedRefreshToken.serialize(), TokenType.BEARER, jwtProperties.getRefreshTokenExpiresIn());
    }

    @Override
    public ValidationResult validateAccessToken(String accessToken) {
        var parseResult = parser.parse(accessToken);

        if (parseResult.isFailed()) {
            return ValidationResult.invalid(parseResult.error());
        }

        return validator.validate(parseResult.token());
    }

    @Override
    public boolean isSupport(JwtProvider jwtProvider) {
        return jwtProvider.equals(JwtProvider.NIMBUS_JOSE);
    }

    private String getJwtId() {
        return UUID.randomUUID().toString();
    }

    private JWSHeader getJWSHeader(JWSAlgorithm jwsAlgorithm) {
        return new JWSHeader(jwsAlgorithm);
    }

    private JWTClaimsSet getClaimsSet(String email, String jwtId, Date issuedAt, Date expirationTime,
                                      @Nullable Set<Role.Authority> authorities) {

        var jwtBuilder = new JWTClaimsSet.Builder()
                .jwtID(jwtId)
                .issuer(jwtProperties.getIssuer())
                .audience(jwtProperties.getAudience())
                .issueTime(issuedAt)
                .expirationTime(expirationTime)
                .subject(email);

        if (authorities != null && !authorities.isEmpty()) {
            jwtBuilder.claim("scope", authorities);
        }

        return jwtBuilder.build();
    }

    private SignedJWT getSignedNimbusJwt(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecretKey secretKey) {
        var jwt = new SignedJWT(jwsHeader, jwtClaimsSet);

        try {
            var accessTokenSigner = new MACSigner(secretKey);
            jwt.sign(accessTokenSigner);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to generate nimbus jwt", e);
        }

        return jwt;
    }

}
