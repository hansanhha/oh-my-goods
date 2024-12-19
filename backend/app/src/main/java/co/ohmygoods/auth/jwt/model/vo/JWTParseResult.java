package co.ohmygoods.auth.jwt.model.vo;

public record JWTParseResult<T>(T token,
                                boolean isFailed,
                                JWTError cause) {

    public static <T> JWTParseResult<T> success(T token) {
        return new JWTParseResult<>(token, false, null);
    }

    public static <T> JWTParseResult<T> failure(JWTError cause) {
        return new JWTParseResult<>(null, true, cause);
    }
}
