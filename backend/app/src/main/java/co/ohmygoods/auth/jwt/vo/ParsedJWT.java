package co.ohmygoods.auth.jwt.vo;

public record ParsedJWT<T>(T token,
                           boolean isFailed,
                           JWTError cause) {

    public static <T> ParsedJWT<T> success(T token) {
        return new ParsedJWT<>(token, false, null);
    }

    public static <T> ParsedJWT<T> failure(JWTError cause) {
        return new ParsedJWT<>(null, true, cause);
    }
}
