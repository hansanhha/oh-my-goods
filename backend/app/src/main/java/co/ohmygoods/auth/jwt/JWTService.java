package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.JWTClaimsKey;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.jwt.vo.JwtValidationResult;

import java.util.Map;

public interface JWTService {

    JWTs generate(Map<JWTClaimsKey, Object> claims);

    JWTs regenerate(String refreshToken);

    void deleteAllByEmail(String email);

    JwtValidationResult validateToken(String token);
}
