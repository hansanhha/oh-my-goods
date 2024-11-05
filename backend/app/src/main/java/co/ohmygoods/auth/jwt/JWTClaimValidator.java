package co.ohmygoods.auth.jwt;

import co.ohmygoods.domain.jwt.vo.JWTValidationResult;

public interface JWTClaimValidator<T> {

    JWTValidationResult validate(T t);
}
