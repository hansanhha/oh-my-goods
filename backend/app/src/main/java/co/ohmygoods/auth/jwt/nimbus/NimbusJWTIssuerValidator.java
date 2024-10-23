package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTValidator;
import co.ohmygoods.auth.jwt.vo.JWTError;
import co.ohmygoods.auth.jwt.vo.JwtValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;

@RequiredArgsConstructor
public class NimbusJWTIssuerValidator implements JWTValidator<JWT> {

    private final String issuer;

    @Override
    public JwtValidationResult validate(JWT jwt) {
        try {
            if (jwt.getJWTClaimsSet().getIssuer().equals(issuer)) {
                return JwtValidationResult.success();
            }

            return JwtValidationResult.error(JWTError.INVALID_ISSUER);
        } catch (ParseException e) {
            return JwtValidationResult.error(JWTError.MALFORMED);
        }
    }
}
