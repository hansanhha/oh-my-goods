package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;

public interface JwtValidator<T> {

    JwtValidationResult validate(T t);
}
