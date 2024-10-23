package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.JwtClaimsKey;
import co.ohmygoods.auth.jwt.vo.JWTs;
import co.ohmygoods.auth.jwt.vo.JwtValidationResult;

import java.util.Map;

public interface JwtService {

    JWTs generate(Map<JwtClaimsKey, Object> claims);

    JWTs regenerate(String refreshToken);

    void deleteAllByEmail(String email);

    JwtValidationResult validateToken(String token);
}
