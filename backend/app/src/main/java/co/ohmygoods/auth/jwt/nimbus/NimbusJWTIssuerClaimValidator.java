package co.ohmygoods.auth.jwt.nimbus;

import co.ohmygoods.auth.jwt.JWTClaimValidator;
import co.ohmygoods.auth.jwt.vo.JWTError;
import co.ohmygoods.auth.jwt.vo.JWTValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;

import java.text.ParseException;

@RequiredArgsConstructor
public class NimbusJWTIssuerClaimValidator implements JWTClaimValidator<JWT> {

    private final String issuer;

    @Override
    public JWTValidationResult validate(JWT jwt) {
        try {
            if (jwt.getJWTClaimsSet().getIssuer().equals(issuer)) {
                return JWTValidationResult.success();
            }

            return JWTValidationResult.error(JWTError.INVALID_ISSUER);
        } catch (ParseException e) {
            return JWTValidationResult.error(JWTError.MALFORMED);
        }
    }
}
