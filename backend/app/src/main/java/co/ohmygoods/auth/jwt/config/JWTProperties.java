package co.ohmygoods.auth.jwt.config;

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
public class JWTProperties {

    @NotNull
    private SecretKey accessTokenKey;

    @NotNull
    private SecretKey refreshTokenKey;

    @NotEmpty
    private String issuer;

    @NotNull
    private JWSAlgorithm accessTokenAlgorithm;

    @NotNull
    private JWSAlgorithm refreshTokenAlgorithm;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration accessTokenExpiresIn;

    @NotNull
    @DurationMin(seconds = 1)
    private Duration refreshTokenExpiresIn;

    private String audience;

    public void setAccessTokenAlgorithm(@NotNull String accessTokenAlgorithm) {
        this.accessTokenAlgorithm = JWSAlgorithm.parse(accessTokenAlgorithm);
    }

    public void setRefreshTokenAlgorithm(@NotNull String refreshTokenAlgorithm) {
        this.accessTokenAlgorithm = JWSAlgorithm.parse(refreshTokenAlgorithm);
    }

    public void setAccessTokenKey(@NotNull String key) {
        var jwk = new OctetSequenceKey.Builder(key.getBytes())
                .algorithm(accessTokenAlgorithm)
                .build();

        this.accessTokenKey = jwk.toSecretKey();
    }

    public void setRefreshTokenKey(@NotNull String key) {
        var jwk = new OctetSequenceKey.Builder(key.getBytes())
                .algorithm(accessTokenAlgorithm)
                .build();

        this.refreshTokenKey= jwk.toSecretKey();
    }
}
