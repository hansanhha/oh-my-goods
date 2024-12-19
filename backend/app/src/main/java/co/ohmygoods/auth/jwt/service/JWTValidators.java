package co.ohmygoods.auth.jwt.service;

import co.ohmygoods.auth.jwt.service.nimbus.DelegatingNimbusJWTClaimValidator;
import co.ohmygoods.auth.jwt.service.nimbus.NimbusJWTExpirationClaimValidator;
import co.ohmygoods.auth.jwt.service.nimbus.NimbusJWTIssuerClaimValidator;
import com.nimbusds.jwt.JWT;

import java.util.List;

public final class JWTValidators {

    public static JWTClaimValidator<JWT> createNimbusJWTDefault() {
        return new DelegatingNimbusJWTClaimValidator(List.of(new NimbusJWTExpirationClaimValidator()));
    }

    public static JWTClaimValidator<JWT> createNimbusJWTDefaultWithIssuer(String issuer) {
        return new DelegatingNimbusJWTClaimValidator(List.of(new NimbusJWTExpirationClaimValidator(), new NimbusJWTIssuerClaimValidator(issuer)));
    }
}
