package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.jwt.model.vo.JWTError;

public record JwtValidationResult(boolean isValid,
                                  JWTError error) {

    public static JwtValidationResult invalid(JWTError error) {
        return new JwtValidationResult(false,  error);
    }

    public static JwtValidationResult valid() {
        return new JwtValidationResult(true, null);
    }
}
