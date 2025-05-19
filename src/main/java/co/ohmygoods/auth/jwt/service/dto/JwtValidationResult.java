package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.exception.AuthException;

import java.util.Map;

public record JWTValidationResult(
        Map<String, Object> claims,
        boolean isValid,
        boolean isParseFailed,
        AuthException error) {

    public static JWTValidationResult parseFailure() {
        return new JWTValidationResult(null,false, true, AuthException.INVALID_JWT);
    }

    public static JWTValidationResult invalid(AuthException error) {
        return new JWTValidationResult(null, false, false, error);
    }

    public static JWTValidationResult valid(Map<String, Object> claims) {
        return new JWTValidationResult(claims, true, false, null);
    }

    public String getSubClaim() {
        return (String) claims.get("sub");
    }

    public Role getRoleClaim() {
        return Role.valueOf(((String) claims.get("role")).toUpperCase());
    }

}
