package co.ohmygoods.auth.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
public class SecurityConfigProperties {

    @Bean
    public SignUrlProperties signUrlProperties() {
        return new SignUrlProperties();
    }

    @Bean
    public CorsProperties corsProperties() {
        return new CorsProperties();
    }

    @Getter
    @Setter
    @Validated
    @ConfigurationProperties(prefix = "application.security.sign-url")
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

    @Getter
    @Setter
    @Validated
    @ConfigurationProperties(prefix = "application.security.cors")
    public static class CorsProperties {

        @NotNull
        private List<String> accessControlAllowOrigin;

        @NotNull
        private List<String> accessControlAllowHeaders;

        @NotNull
        private List<String> accessControlAllowMethods;

        @NotNull
        private List<String> accessControlExposeHeaders;

        @NotNull
        private boolean accessControlAllowCredentials;
    }

    @Getter
    @Setter
    @Validated
    @ConfigurationProperties(prefix = "application.security.whitelist")
    public static class WhitelistProperties {

        @NotNull
        private List<String> whiteServletPathList;
    }

}
