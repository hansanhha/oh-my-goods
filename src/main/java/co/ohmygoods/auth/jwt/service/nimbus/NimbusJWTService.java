package co.ohmygoods.auth.jwt.service.nimbus;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.config.JWTProperties;
import co.ohmygoods.auth.jwt.model.vo.JWTProvider;
import co.ohmygoods.auth.jwt.model.vo.TokenType;
import co.ohmygoods.auth.jwt.service.JWTService;
import co.ohmygoods.auth.jwt.service.RefreshTokenService;
import co.ohmygoods.auth.jwt.service.dto.JWT;
import co.ohmygoods.auth.jwt.service.dto.JWTs;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class NimbusJWTService implements JWTService {
    
    private final AccountRepository accountRepository;
    private final JWTProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;

    @Override
    public JWTs generateToken(String memberId, Role role) {      
        refreshTokenService.remove(memberId);

        Instant issueTime = Instant.now();

        JWT accessToken = buildAccessToken(memberId, role, issueTime);
        JWT refreshToken = buildRefreshToken(memberId, role, issueTime);

        Duration refreshTokenTTL = getRefreshTokenTTL(issueTime, refreshToken.expiresIn());
        refreshTokenService.save(memberId, refreshToken.tokenValue(), refreshTokenTTL);

        return new JWTs(accessToken, refreshToken);
    }

    @Override
    public JWTs regenerate(String memberId, String refreshTokenValue) {
        refreshTokenService.validateStealToken(memberId, refreshTokenValue);
        refreshTokenService.remove(memberId);

        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        Instant issueTime = Instant.now();

        JWT accessToken = buildAccessToken(memberId, account.getRole(), issueTime);
        JWT refreshToken = buildRefreshToken(memberId, account.getRole(), issueTime);

        Duration refreshTokenTTL = getRefreshTokenTTL(issueTime, refreshToken.expiresIn());
        refreshTokenService.save(memberId, refreshToken.tokenValue(), refreshTokenTTL);        

        return new JWTs(accessToken, refreshToken);
    }
    
    @Override
    public void removeRefreshToken(String memberId) {
        refreshTokenService.remove(memberId);
    }
    
    @Override
    public boolean isSupport(JWTProvider jwtProvider) {
        return JWTProvider.NIMBUS_JOSE.equals(jwtProvider);
    }

    private Duration getRefreshTokenTTL(Instant issueTime, Instant refreshTokenExpiresIn) {
        return Duration.between(issueTime, refreshTokenExpiresIn);
    }

    private JWT buildAccessToken(String memberId, Role role, Instant issueTime) {
        Instant accessTokenExpirationTime = issueTime.plus(jwtProperties.getAccessTokenExpiresIn());
        JWTClaimsSet accessTokenClaimsSet = getClaimsSet(memberId, role, issueTime, accessTokenExpirationTime);
        JWSHeader accessTokenJWSHeader = new JWSHeader(jwtProperties.getAccessTokenAlgorithm());
        SignedJWT nimbusAccessToken = buildNimbusJWT(accessTokenJWSHeader, accessTokenClaimsSet, jwtProperties.getAccessTokenKey());
        return new JWT(nimbusAccessToken.serialize(), TokenType.BEARER, accessTokenExpirationTime);
    }

    private JWT buildRefreshToken(String memberId, Role role, Instant issueTime) {
        Instant refreshTokenExpirationTime = issueTime.plus(jwtProperties.getRefreshTokenExpiresIn());
        JWTClaimsSet refreshTokenClaimsSet = getClaimsSet(memberId, role, issueTime, refreshTokenExpirationTime);
        JWSHeader refreshTokenJWTHeader = new JWSHeader(jwtProperties.getRefreshTokenAlgorithm());
        SignedJWT nimbusRefreshToken = buildNimbusJWT(refreshTokenJWTHeader, refreshTokenClaimsSet, jwtProperties.getRefreshTokenKey());       
        return new JWT(nimbusRefreshToken.serialize(), TokenType.BEARER, refreshTokenExpirationTime); 
    }

    private JWTClaimsSet getClaimsSet(String memberId, Role role, Instant issueTime, Instant expirationTime) {
        return new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer(jwtProperties.getIssuer())
                .audience(jwtProperties.getAudience())
                .issueTime(Date.from(issueTime))
                .expirationTime(Date.from(expirationTime))
                .claim("role", role)
                .subject(memberId)
                .build();
    }

    private SignedJWT buildNimbusJWT(JWSHeader jwsHeader, JWTClaimsSet jwtClaimsSet, SecretKey secretKey) {
        var jwt = new SignedJWT(jwsHeader, jwtClaimsSet);

        try {
            MACSigner signer = new MACSigner(secretKey);
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw AuthException.FAILED_SIGN_NIMBUS_JWT;
        }

        return jwt;
    }

}
