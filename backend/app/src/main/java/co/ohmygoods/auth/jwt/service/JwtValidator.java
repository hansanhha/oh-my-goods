package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;

/**
 * <p>
 * 토큰의 유효성을 검증하는 인터페이스, jwt 라이브러리에 따라 구현체를 제공함
 * </p>
 *
 */
public interface JwtValidator {

    JwtValidationResult validate(String token);

    enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN;
    }
}
