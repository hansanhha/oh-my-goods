package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.jwt.model.vo.JWTError;

public record JwtParseResult<T>(T token,
                                boolean isFailed,
                                JWTError error) {

    public static <T> JwtParseResult<T> success(T token) {
        return new JwtParseResult<>(token, false, null);
    }

    public static <T> JwtParseResult<T> failure(JWTError error) {
        return new JwtParseResult<>(null, true, error);
    }
}
