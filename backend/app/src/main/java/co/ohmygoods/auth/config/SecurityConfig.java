package co.ohmygoods.auth.config;

import co.ohmygoods.auth.jwt.JWTBearerAuthenticationFilter;
import co.ohmygoods.auth.jwt.JsonAccessDeniedHandler;
import co.ohmygoods.auth.jwt.JsonAuthenticationEntryPoint;
import co.ohmygoods.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import co.ohmygoods.auth.oauth2.OAuth2AuthorizationService;
import co.ohmygoods.auth.oauth2.OAuth2UserPrincipalService;
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

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2UserPrincipalService oAuth2UserPrincipalService;
    private final OAuth2AuthorizationService oAuth2AuthorizationService;
    private final SecurityConfigProperties.SignUrlProperties signUrlProperties;

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
                .cors(cors -> cors.configurationSource(testCorsConfigurationSource()))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("*").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginProcessingUrl(signUrlProperties.getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserPrincipalService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(signUrlProperties.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl(signUrlProperties.getLogoutUrl())
                        .addLogoutHandler(oAuth2AuthorizationService)
                        .clearAuthentication(true)
                        .logoutSuccessUrl(signUrlProperties.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint)
                        .accessDeniedHandler(jsonAccessDeniedHandler))
                .build();
    }

    @Bean
    public CorsConfigurationSource testCorsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            config.setAllowedOriginPatterns(List.of("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }
}
