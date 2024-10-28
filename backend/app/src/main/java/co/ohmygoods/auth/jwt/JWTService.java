package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.JWTClaimsKey;
import co.ohmygoods.auth.jwt.vo.JWTInfo;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.jwt.vo.JWTValidationResult;

import java.util.Map;
import java.util.Optional;

public interface JWTService {

    JWTs generate(Map<JWTClaimsKey, Object> claims);

    JWTs regenerate(String refreshToken);

    Optional<JWTInfo> extractTokenInfo(String token);

    void revokeRefreshToken(String accessToken);

    JWTValidationResult validateToken(String token);
}
