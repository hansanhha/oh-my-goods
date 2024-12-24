package co.ohmygoods.auth.jwt.service.nimbus.validation;

import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Qualifier("nimbusJoseJwtValidatorDelegate")
@Component
@RequiredArgsConstructor
public class NimbusJoseJwtValidatorDelegate implements JwtValidator<JWT> {

    private static final boolean INVALID_RESULT = false;
    private final List<JwtValidator<JWT>> nimbusJoseJwtValidators;

    @Override
    public JwtValidationResult validate(JWT jwt) {
        Map<Boolean, List<JwtValidationResult>> results = nimbusJoseJwtValidators.stream()
                .map(jwtValidator -> jwtValidator.validate(jwt))
                .collect(Collectors.groupingBy(JwtValidationResult::isValid));

        if (!results.get(INVALID_RESULT).isEmpty()) {
            return results.get(INVALID_RESULT).getFirst();
        }

        return JwtValidationResult.valid();
    }
}
