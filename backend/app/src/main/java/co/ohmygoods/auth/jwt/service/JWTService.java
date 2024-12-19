package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.model.vo.JWTClaimsKey;
import co.ohmygoods.auth.jwt.model.vo.JWTInfo;
import co.ohmygoods.auth.jwt.model.vo.JWTs;
import co.ohmygoods.auth.jwt.model.vo.JWTValidationResult;

import java.util.Map;
import java.util.Optional;

public interface JWTService {

    JWTs generate(Map<JWTClaimsKey, Object> claims);

    JWTs regenerate(String refreshToken);

    Optional<JWTInfo> extractTokenInfo(String token);

    void revokeRefreshToken(String accessToken);

    JWTValidationResult validateToken(String token);
}
