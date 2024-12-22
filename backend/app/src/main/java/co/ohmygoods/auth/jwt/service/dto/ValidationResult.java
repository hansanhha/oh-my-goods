package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.jwt.model.vo.JWTError;

public record ValidationResult(boolean isValid,
                               JWTError error) {

    public static ValidationResult invalid(JWTError error) {
        return new ValidationResult(false,  error);
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }
}
