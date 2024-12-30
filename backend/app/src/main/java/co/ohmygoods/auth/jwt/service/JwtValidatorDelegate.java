package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class JwtValidatorDelegate implements JwtValidator {

    private final List<JwtValidator> validators;

    @Override
    public JwtValidationResult validate(String token) {
        List<JwtValidationResult> results = validators.stream()
                .map(validator -> validator.validate(token))
                .toList();

        if (results.stream().allMatch(JwtValidationResult::isParseFailed)) {
            return JwtValidationResult.parseFailure();
        }

        return results.stream()
                .filter(JwtValidationResult::isValid)
                .findFirst()
                .orElse(results.getFirst());
    }
}
