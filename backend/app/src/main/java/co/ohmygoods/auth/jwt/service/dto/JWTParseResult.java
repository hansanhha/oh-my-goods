package co.ohmygoods.auth.jwt.service.dto;

import co.ohmygoods.auth.jwt.model.vo.JWTError;

public record JWTParseResult<T>(T token,
                                boolean isFailed,
                                JWTError error) {

    public static <T> JWTParseResult<T> success(T token) {
        return new JWTParseResult<>(token, false, null);
    }

    public static <T> JWTParseResult<T> failure(JWTError error) {
        return new JWTParseResult<>(null, true, error);
    }
}
