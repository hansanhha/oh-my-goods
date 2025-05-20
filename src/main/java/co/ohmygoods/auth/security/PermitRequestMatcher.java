package co.ohmygoods.auth.security;


import co.ohmygoods.auth.security.config.SecurityConfigProperties;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PermitRequestMatcher {

    private static final List<RequestMatcher> requestMatchers = new ArrayList<>();

    private final SecurityConfigProperties securityProperties;

    @PostConstruct
    void init() {
        securityProperties.getWhiteList().forEach(pattern -> requestMatchers.add(AntPathRequestMatcher.antMatcher(pattern)));
        SecurityConfigProperties.OAuth2Url signUrl = securityProperties.getSign();
        requestMatchers.add(AntPathRequestMatcher.antMatcher(signUrl.getOauth2AuthorizationBaseUrl()));
        requestMatchers.add(AntPathRequestMatcher.antMatcher(signUrl.getOauth2AuthorizationProcessingUrl()));
        requestMatchers.add(AntPathRequestMatcher.antMatcher(signUrl.getOauth2LoginProcessingUrl()));
    }

    public boolean matches(HttpServletRequest request) {
        return requestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
