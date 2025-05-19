package co.ohmygoods.auth.security.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "application.security")
@Getter
@Setter
@Validated
public class SecurityConfigProperties {

    private List<String> whiteList;

    private OAuth2Url sign;

    private CorsProperties cors;

    @Getter
    @Setter
    @Validated
    public static class OAuth2Url {

        @NotEmpty
        private String oauth2AuthorizationBaseUrl;

        @NotEmpty
        private String oauth2AuthorizationProcessingUrl;

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
    public static class CorsProperties {

        @NotNull
        private List<String> accessControlAllowOrigin;

        @NotNull
        private List<String> accessControlAllowHeaders;

        @NotNull
        private List<String> accessControlAllowMethods;

        @NotNull
        private List<String> accessControlExposeHeaders;

        private boolean accessControlAllowCredentials;
        
    }
}
