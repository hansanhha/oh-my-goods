package co.ohmygoods.auth.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
public class SecurityConfigProperties {

    @Bean
    @ConfigurationProperties(prefix = "application.security.sign-url")
    public SignUrlProperties signUrlProperties() {
        return new SignUrlProperties();
    }

    @Getter
    @Setter
    @Validated
    public static class SignUrlProperties {
        @NotEmpty
        private String oauth2AuthorizationBaseUrl;

        @NotEmpty
        private String oauth2LoginProcessingUrl;

        @NotEmpty
        private String logoutUrl;

        @NotEmpty
        private String logoutRedirectUrl;
    }



}
