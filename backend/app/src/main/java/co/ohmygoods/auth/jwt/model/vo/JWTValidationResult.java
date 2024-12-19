package co.ohmygoods.auth.jwt.model.vo;

public record JWTValidationResult(boolean hasError,
                                  JWTInfo jwtInfo,
                                  JWTError error) {

    public static JWTValidationResult success() {
        return new JWTValidationResult(false, null, null);
    }

    public static JWTValidationResult error(JWTError jwtError) {
        return new JWTValidationResult(true, null, jwtError);
    }

    public static JWTValidationResult valid(JWTInfo jwtInfo) {
        return new JWTValidationResult(false, jwtInfo, null);
    }
}
