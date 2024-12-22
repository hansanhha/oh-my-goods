package co.ohmygoods.auth.web.security.config;

import co.ohmygoods.auth.web.security.HttpErrorExceptionHandleFilter;
import co.ohmygoods.auth.web.security.JWTBearerAuthenticationFilter;
import co.ohmygoods.auth.web.security.JsonAccessDeniedHandler;
import co.ohmygoods.auth.web.security.JsonAuthenticationEntryPoint;
import co.ohmygoods.auth.web.security.OAuth2AuthenticationSuccessHandler;
import co.ohmygoods.auth.web.security.oauth2.OAuth2UserPrincipalService;
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

    private final HttpErrorExceptionHandleFilter httpErrorExceptionHandleFilter;
    private final JWTBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final JsonAuthenticationEntryPoint jsonAuthenticationEntryPoint;
    private final JsonAccessDeniedHandler jsonAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2UserPrincipalService oAuth2UserPrincipalService;
    private final SecurityConfigProperties.SignUrlProperties signUrlProperties;
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
                        .requestMatchers(signUrlProperties.getOauth2AuthorizationBaseUrl()).permitAll()
                        .requestMatchers(signUrlProperties.getOauth2LoginProcessingUrl()).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginProcessingUrl(signUrlProperties.getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserPrincipalService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(signUrlProperties.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .logout(logout -> logout
                        .logoutUrl(signUrlProperties.getLogoutUrl())
                        .clearAuthentication(true)
                        .logoutSuccessUrl(signUrlProperties.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(httpErrorExceptionHandleFilter, JWTBearerAuthenticationFilter.class)
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
