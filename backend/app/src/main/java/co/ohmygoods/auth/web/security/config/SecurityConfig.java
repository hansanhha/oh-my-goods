package co.ohmygoods.auth.web.security.config;

import co.ohmygoods.auth.oauth2.service.CacheableOAuth2AuthorizedClientService;
import co.ohmygoods.auth.oauth2.service.IdentifiedOAuth2UserService;
import co.ohmygoods.auth.web.security.*;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final IdentifiedOAuth2UserService identifiedOAuth2UserService;
    private final CacheableOAuth2AuthorizedClientService cacheableOAuth2AuthorizedClientService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final HttpErrorExceptionHandleFilter httpErrorExceptionHandleFilter;
    private final JwtBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;

    private final SecurityConfigProperties.Whitelist whitelist;
    private final SecurityConfigProperties.CorsProperties corsProperties;
    private final SecurityConfigProperties.WhitelistProperties whitelistProperties;

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
                .cors(cors -> cors.configurationSource(buildCorsConfigurationSource(corsProperties)))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(whitelistProperties.getWhiteServletPathList().toArray(new String[0])).permitAll()
                        .requestMatchers(whitelist.getOauth2AuthorizationBaseUrl()).permitAll()
                        .requestMatchers(whitelist.getOauth2LoginProcessingUrl()).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginProcessingUrl(whitelist.getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(identifiedOAuth2UserService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(whitelist.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .oauth2Client(oauth2Client -> oauth2Client
                        .authorizedClientService(cacheableOAuth2AuthorizedClientService))
                .logout(logout -> logout
                        .logoutUrl(whitelist.getLogoutUrl())
                        .clearAuthentication(true)
                        .logoutSuccessUrl(whitelist.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(httpErrorExceptionHandleFilter, JwtBearerAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
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
