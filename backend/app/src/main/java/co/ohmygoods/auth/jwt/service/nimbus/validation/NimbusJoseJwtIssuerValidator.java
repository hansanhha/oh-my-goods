package co.ohmygoods.auth.jwt.service.nimbus.validation;

import co.ohmygoods.auth.jwt.model.vo.JWTError;
import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.ValidationResult;
import com.nimbusds.jwt.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class NimbusJoseJwtIssuerValidator implements JwtValidator<JWT> {

    @Value("${application.security.jwt.issuer}")
    private String issuer;

    @Override
    public ValidationResult validate(JWT jwt) {
        try {
            if (jwt.getJWTClaimsSet().getIssuer().equals(issuer)) {
                return ValidationResult.valid();
            }

            return ValidationResult.invalid(JWTError.INVALID_ISSUER);
        } catch (ParseException e) {
            return ValidationResult.invalid(JWTError.MALFORMED);
        }
    }
}
