package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.exception.AuthError;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Component
@RequiredArgsConstructor
public class JwtValidatorDelegate implements JwtValidator {

    private static final boolean ALL_VALIDATORS_INVALID_RESULT = false;
    private static final boolean VALID_RESULT = true;

    private final List<JwtValidator> validators;

    @Override
    public JwtValidationResult validate(String token) {
        List<JwtValidationResult> results = validators.stream()
                .map(validator -> validator.validate(token))
                .toList();

        Map<Boolean, List<JwtValidationResult>> groupedResults = results.stream().collect(Collectors.groupingBy(JwtValidationResult::isValid));

        if (results.stream().allMatch(JwtValidationResult::isParseFailed)) {
            return JwtValidationResult.parseFailure();
        }

        if (groupedResults.isEmpty()) {
            return JwtValidationResult.invalid(AuthError.INVALID_JWT);
        }

        List<JwtValidationResult> invalidResults = groupedResults.get(ALL_VALIDATORS_INVALID_RESULT);

        return invalidResults.isEmpty()
                ? groupedResults.get(VALID_RESULT).getFirst()
                : invalidResults.getFirst();
    }
}
