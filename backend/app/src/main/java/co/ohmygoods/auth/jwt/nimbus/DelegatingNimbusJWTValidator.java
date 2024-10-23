package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTValidator;
import co.ohmygoods.auth.jwt.vo.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DelegatingNimbusJWTValidator implements JWTValidator<JWT> {

    private final List<JWTValidator<JWT>> jwtValidators;

    @Override
    public JwtValidationResult validate(JWT jwt) {
        return jwtValidators.stream()
                .map(jwtValidator -> jwtValidator.validate(jwt))
                .filter(JwtValidationResult::hasError)
                .findFirst()
                .orElse(JwtValidationResult.success());
    }
}
