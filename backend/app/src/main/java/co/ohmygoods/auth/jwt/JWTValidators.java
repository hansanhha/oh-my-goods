package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.jwt.nimbus.DelegatingNimbusJWTValidator;
import co.ohmygoods.auth.jwt.nimbus.NimbusJWTExpirationValidator;
import co.ohmygoods.auth.jwt.nimbus.NimbusJWTIssuerValidator;
import com.nimbusds.jwt.JWT;

import java.util.List;

public final class JWTValidators {

    public static JWTValidator<JWT> createNimbusJWTDefault() {
        return new DelegatingNimbusJWTValidator(List.of(new NimbusJWTExpirationValidator()));
    }

    public static JWTValidator<JWT> createNimbusJWTDefaultWithIssuer(String issuer) {
        return new DelegatingNimbusJWTValidator(List.of(new NimbusJWTExpirationValidator(), new NimbusJWTIssuerValidator(issuer)));
    }
}
