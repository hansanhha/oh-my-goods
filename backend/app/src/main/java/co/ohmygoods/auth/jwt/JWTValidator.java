package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.vo.JwtValidationResult;

public interface JWTValidator<T> {

    JwtValidationResult validate(T t);
}
