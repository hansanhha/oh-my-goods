package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTClaimValidator;
import co.ohmygoods.auth.jwt.vo.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DelegatingNimbusJWTClaimValidator implements JWTClaimValidator<JWT> {

    private final List<JWTClaimValidator<JWT>> jwtClaimValidators;

    @Override
    public JwtValidationResult validate(JWT jwt) {
        return jwtClaimValidators.stream()
                .map(jwtValidator -> jwtValidator.validate(jwt))
                .filter(JwtValidationResult::hasError)
                .findFirst()
                .orElse(JwtValidationResult.success());
    }
}
