package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JWTValidationResult;

/**
 * <p>
 * 토큰의 유효성을 검증하는 인터페이스, jwt 라이브러리에 따라 구현체를 제공함
 * </p>
 *
 * 주어진 string token 값을 파싱해야 하며 파싱에 실패한 경우 JwtValidationResult.parseFailure 반환
 */
public interface JWTValidator {

    JWTValidationResult validate(String token);

}
