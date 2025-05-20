package co.ohmygoods.auth.security.config;


import co.ohmygoods.auth.account.model.vo.Role;
import co.ohmygoods.auth.oauth2.service.OAuth2UserLoginService;
import co.ohmygoods.auth.security.JwtBearerAuthenticationFilter;
import co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler;
import co.ohmygoods.auth.security.PermitRequestMatcher;
import co.ohmygoods.auth.security.SecurityExceptionProcessingFilter;
import co.ohmygoods.auth.security.config.SecurityConfigProperties.OAuth2Url;
import co.ohmygoods.global.logging.RequestProcessingLoggingInterceptor;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityConfigProperties securityProperties;

    private final OAuth2UserLoginService oAuth2UserLoginService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final JwtBearerAuthenticationFilter jwtBearerAuthenticationFilter;
    private final SecurityExceptionProcessingFilter securityExceptionProcessingFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, RequestProcessingLoggingInterceptor requestProcessingLoggingInterceptor) throws Exception {
        OAuth2Url signUrl = securityProperties.getSign();

        return http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(RequestCacheConfigurer::disable)
                .rememberMe(RememberMeConfigurer::disable)
                .anonymous(AnonymousConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .cors(config -> config.configurationSource(corsConfigurationSource(securityProperties.getCors())))
                .authorizeHttpRequests(config -> config
                        .requestMatchers(securityProperties.getWhiteList().toArray(new String[0])).permitAll()
                        .requestMatchers(new String[] {signUrl.getOauth2AuthorizationBaseUrl(), signUrl.getOauth2LoginProcessingUrl()}).permitAll()
                        .requestMatchers("/api/seller").hasRole(Role.SELLER.name())
                        .dispatcherTypeMatchers(getPermitDispatcherTypes()).permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(config -> config
                        .loginProcessingUrl(securityProperties.getSign().getOauth2LoginProcessingUrl())
                        .userInfoEndpoint(endpoint -> endpoint.userService(oAuth2UserLoginService))
                        .authorizationEndpoint(endpoint -> endpoint.baseUri(signUrl.getOauth2AuthorizationBaseUrl()))
                        .successHandler(oAuth2AuthenticationSuccessHandler))
                .logout(config -> config
                        .logoutUrl(signUrl.getLogoutUrl())
                        .clearAuthentication(true)
                        .logoutSuccessUrl(signUrl.getLogoutUrl())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .addFilterBefore(jwtBearerAuthenticationFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(securityExceptionProcessingFilter, JwtBearerAuthenticationFilter.class)
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource(SecurityConfigProperties.CorsProperties cors) {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(cors.getAccessControlAllowHeaders());
            config.setAllowedMethods(cors.getAccessControlAllowMethods());
            config.setAllowedOriginPatterns(cors.getAccessControlAllowOrigin());
            config.setExposedHeaders(cors.getAccessControlExposeHeaders());
            config.setAllowCredentials(cors.isAccessControlAllowCredentials());
            return config;
        };
    }

    private DispatcherType[] getPermitDispatcherTypes() {
        return new DispatcherType[] {
                DispatcherType.ERROR, DispatcherType.FORWARD
        };
    }

}
