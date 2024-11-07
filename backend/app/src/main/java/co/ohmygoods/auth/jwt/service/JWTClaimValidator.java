package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.vo.JWTValidationResult;

public interface JWTClaimValidator<T> {

    JWTValidationResult validate(T t);
}
