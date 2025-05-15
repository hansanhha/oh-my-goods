package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>jwt 라이브러리 별 모든 검증 구현체에게 string token에 대한 검증을 위임하는 객체</p>
 * <p>유효하지 않은 토큰 판정 조건: 모든 검증 구현체에서 파싱을 실패하거나 유효한 토큰 판정을 내린 적이 없는 경우</p>
 * <p>유효한 토큰 판정 조건: 특정 검증 구현체에서 파싱과 검증에 모두 성공한 경우</p>
 */
@Primary
@Component
@RequiredArgsConstructor
public class JwtValidatorDelegate implements JwtValidator {

    private static final boolean VALID_RESULT = true;

    private final List<JwtValidator> validators;

    @Override
    public JwtValidationResult validate(String token) {
        List<JwtValidationResult> results = validators.stream()
                .map(validator -> validator.validate(token))
                .toList();

        List<JwtValidationResult> parsedValidationResult = getParsedValidationResult(results);

        if (parsedValidationResult.isEmpty()) {
            return JwtValidationResult.parseFailure();
        }

        Map<Boolean, List<JwtValidationResult>> grouping = groupingByValidAmongParsed(parsedValidationResult);

        return grouping.get(VALID_RESULT).isEmpty()
            ? grouping.get(!VALID_RESULT).getFirst()
            : grouping.get(VALID_RESULT).getFirst();
    }

    private List<JwtValidationResult> getParsedValidationResult(List<JwtValidationResult> results) {
        return results.stream().filter(result -> !result.isParseFailed()).toList();
    }

    private Map<Boolean, List<JwtValidationResult>> groupingByValidAmongParsed(List<JwtValidationResult> results) {
        return results.stream().collect(Collectors.groupingBy(JwtValidationResult::isValid));
    }

}
