package co.ohmygoods.auth.jwt.service.dto;

public record JWTParseResult<T>(T token,
                                boolean isFailed) {

    public static <T> JWTParseResult<T> success(T token) {
        return new JWTParseResult<>(token, false);
    }

    public static <T> JWTParseResult<T> failure() {
        return new JWTParseResult<>(null, true);
    }
}
