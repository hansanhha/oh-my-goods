package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.exception.AuthException;

import java.util.Map;

public record JwtValidationResult(
        Map<String, Object> claims,
        boolean isValid,
        boolean isParseFailed,
        AuthException error) {

    public static JwtValidationResult parseFailure() {
        return new JwtValidationResult(null,false, true, AuthException.INVALID_JWT);
    }

    public static JwtValidationResult invalid(AuthException error) {
        return new JwtValidationResult(null, false, false, error);
    }

    public static JwtValidationResult valid(Map<String, Object> claims) {
        return new JwtValidationResult(claims, true, false, null);
    }

    public String getSubject() {
        return (String) claims.get("sub");
    }

    public Role getRole() {
        return Role.valueOf(((String) claims.get("role")).toUpperCase());
    }

}
