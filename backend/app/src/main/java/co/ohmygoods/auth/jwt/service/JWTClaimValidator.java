package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.model.vo.JWTValidationResult;

public interface JWTClaimValidator<T> {

    JWTValidationResult validate(T t);
}
