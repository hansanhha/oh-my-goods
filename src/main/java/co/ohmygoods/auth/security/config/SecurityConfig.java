package co.ohmygoods.auth.security.config;


import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.jwt.service.JWTValidator;
import co.ohmygoods.auth.oauth2.service.OAuth2UserLoginService;
import co.ohmygoods.auth.security.JwtBearerAuthenticationFilter;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;
import co.ohmygoods.auth.security.PermitRequestMatcher;
import co.ohmygoods.auth.security.SecurityExceptionProcessingFilter;
import co.ohmygoods.global.logging.RequestProcessingLoggingInterceptor;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.DispatcherType;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AnonymousConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // OAuth2
    private final OAuth2UserLoginService identifiedOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    // Security Properties
    private final SecurityConfigProperties.Whitelist whitelist;
    private final SecurityConfigProperties.CorsProperties corsProperties;

    // Custom Filter
    private JwtBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final JWTValidator jwtValidator;
    private final SecurityExceptionProcessingFilter securityExceptionProcessingFilter;

    @PostConstruct
    void init() {
        jwtBearerAuthenticationFilter  = new JwtBearerAuthenticationFilter(createPermitRequestMatcher(whitelist),jwtValidator);
    }

    private PermitRequestMatcher createPermitRequestMatcher(SecurityConfigProperties.Whitelist whitelist) {
        PermitRequestMatcher permitRequestMatcher = new PermitRequestMatcher(whitelist.getServletPathList());
        permitRequestMatcher.add(whitelist.getOauth2AuthorizationBaseUrl());
        permitRequestMatcher.add(whitelist.getOauth2AuthorizationProcessingUrl());
        permitRequestMatcher.add(whitelist.getOauth2LoginProcessingUrl());
        return permitRequestMatcher;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RequestProcessingLoggingInterceptor requestProcessingLoggingInterceptor) throws Exception {
        return http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)
                .rememberMe(RememberMeConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .cors(config -> config.configurationSource(buildCorsConfigurationSource(corsProperties)))
                .authorizeHttpRequests(config -> config
                        .requestMatchers(whitelist.getServletPathList().toArray(new String[0])).permitAll()
                        .requestMatchers(oAuth2LoginProcessingUrlString()).permitAll()
                        .requestMatchers(sellerRequestMatcher()).hasRole(Role.SELLER.name())
                        .dispatcherTypeMatchers(permitDispatcherTypes()).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(config -> config
                        .loginProcessingUrl(whitelist.getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(identifiedOAuth2UserService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(whitelist.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .logout(config -> config
                        .logoutUrl(whitelist.getLogoutUrl())
                        .clearAuthentication(true)
                        .logoutSuccessUrl(whitelist.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(securityExceptionProcessingFilter, JwtBearerAuthenticationFilter.class)
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

    private DispatcherType[] permitDispatcherTypes() {
        return new DispatcherType[] {
                DispatcherType.ERROR, DispatcherType.FORWARD
        };
    }

    private RequestMatcher sellerRequestMatcher() {
        return request -> request.getServletPath().startsWith("/api/seller");
    }

    private String[] oAuth2LoginProcessingUrlString() {
        return new String[] {
                whitelist.getOauth2AuthorizationBaseUrl(), whitelist.getOauth2LoginProcessingUrl()
        };
    }
}
