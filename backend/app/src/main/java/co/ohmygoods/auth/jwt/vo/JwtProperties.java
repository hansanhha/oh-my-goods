package co.ohmygoods.auth.jwt.vo;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.crypto.SecretKey;
import java.time.Duration;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    @NotNull
    private SecretKey key;

    @NotEmpty
    private String issuer;

    @NotNull
    private JWSAlgorithm algorithm;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration accessTokenExpiresIn;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration refreshTokenExpiresIn;

    private String audience;

    public void setAlgorithm(@NotNull String algorithm) {
        this.algorithm = JWSAlgorithm.parse(algorithm);
    }

    public void setKey(@NotNull String key) {
        var jwk = new OctetSequenceKey.Builder(key.getBytes())
                .algorithm(algorithm)
                .build();

        this.key = jwk.toSecretKey();
    }
}
