package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.JwtClaimsKey;
import co.ohmygoods.auth.jwt.vo.JWTs;

import java.util.Map;

public interface JwtService {

    JWTs generate(Map<JwtClaimsKey, Object> claims);

    JWTs regenerate(String refreshToken);

    ValidationResult validate(String accessToken);
}
