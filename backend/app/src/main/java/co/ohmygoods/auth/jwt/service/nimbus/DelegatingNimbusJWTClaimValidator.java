package co.ohmygoods.auth.jwt.service.nimbus;

import co.ohmygoods.auth.jwt.service.JWTClaimValidator;
import co.ohmygoods.auth.jwt.model.vo.JWTValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DelegatingNimbusJWTClaimValidator implements JWTClaimValidator<JWT> {

    private final List<JWTClaimValidator<JWT>> jwtClaimValidators;

    @Override
    public JWTValidationResult validate(JWT jwt) {
        return jwtClaimValidators.stream()
                .map(jwtValidator -> jwtValidator.validate(jwt))
                .filter(JWTValidationResult::hasError)
                .findFirst()
                .orElse(JWTValidationResult.success());
    }
}
