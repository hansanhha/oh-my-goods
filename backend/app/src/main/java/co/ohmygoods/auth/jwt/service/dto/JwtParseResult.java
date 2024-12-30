package co.ohmygoods.auth.jwt.service.dto;

public record JwtParseResult<T>(T token,
                                boolean isFailed) {

    public static <T> JwtParseResult<T> success(T token) {
        return new JwtParseResult<>(token, false);
    }

    public static <T> JwtParseResult<T> failure() {
        return new JwtParseResult<>(null, true);
    }
}
