package co.ohmygoods.auth.jwt.vo;

public record JwtValidationResult(boolean hasError,
                                  JWTInfo jwtInfo,
                                  JWTError error) {

    public static JwtValidationResult success() {
        return new JwtValidationResult(false, null, null);
    }

    public static JwtValidationResult error(JWTError jwtError) {
        return new JwtValidationResult(true, null, jwtError);
    }

    public static JwtValidationResult valid(JWTInfo jwtInfo) {
        return new JwtValidationResult(false, jwtInfo, null);
    }
}
