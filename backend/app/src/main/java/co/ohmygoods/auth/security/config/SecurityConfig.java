package co.ohmygoods.auth.security.config;

import co.ohmygoods.auth.oauth2.service.CacheableOAuth2AuthorizedClientService;
import co.ohmygoods.auth.oauth2.service.IdentifiedOAuth2UserService;
import co.ohmygoods.auth.security.JwtBearerAuthenticationFilter;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;
import co.ohmygoods.auth.security.SecurityExceptionProcessingFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2
    private final IdentifiedOAuth2UserService identifiedOAuth2UserService;
    private final CacheableOAuth2AuthorizedClientService cacheableOAuth2AuthorizedClientService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // Custom Filter
    private final JwtBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final SecurityExceptionProcessingFilter securityExceptionProcessingFilter;

    // Security Properties
    private final SecurityConfigProperties.Whitelist whitelist;
    private final SecurityConfigProperties.CorsProperties corsProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(SessionManagementConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .rememberMe(RememberMeConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .cors(config -> config.configurationSource(buildCorsConfigurationSource(corsProperties)))
                .authorizeHttpRequests(config -> config
                        .requestMatchers(whitelist.getOauth2AuthorizationBaseUrl()).permitAll()
                        .requestMatchers(whitelist.getOauth2LoginProcessingUrl()).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(config -> config
                        .loginProcessingUrl(whitelist.getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(identifiedOAuth2UserService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(whitelist.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .oauth2Client(config -> config
                        .authorizedClientService(cacheableOAuth2AuthorizedClientService))
                .logout(config -> config
                        .logoutUrl(whitelist.getLogoutUrl())
                        .clearAuthentication(true)
                        .logoutSuccessUrl(whitelist.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(securityExceptionProcessingFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .build();
    }

    private CorsConfigurationSource buildCorsConfigurationSource(SecurityConfigProperties.CorsProperties corsProperties) {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(corsProperties.getAccessControlAllowHeaders());
            config.setAllowedMethods(corsProperties.getAccessControlAllowMethods());
            config.setAllowedOriginPatterns(corsProperties.getAccessControlAllowOrigin());
            config.setExposedHeaders(corsProperties.getAccessControlExposeHeaders());
            config.setAllowCredentials(corsProperties.isAccessControlAllowCredentials());
            return config;
        };
    }
}
