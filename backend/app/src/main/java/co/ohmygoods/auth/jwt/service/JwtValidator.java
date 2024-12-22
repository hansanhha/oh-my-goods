package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.dto.ValidationResult;

public interface JwtValidator<T> {

    ValidationResult validate(T t);
}
