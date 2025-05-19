package co.ohmygoods.auth.security;


import java.io.IOException;
import java.net.URI;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import static co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler.ACCESS_TOKEN_REQUEST_ATTRIBUTE_NAME;
import static co.ohmygoods.auth.security.OAuth2AuthenticationSuccessHandler.REFRESH_TOKEN_REQUEST_ATTRIBUTE_NAME;


@Component
public class OAuth2LoginSuccessRedirectHandler implements RedirectStrategy {

    @Value("${application.security.oauth2.success-redirect-base-url}")
    private String successRedirectBaseUrl;

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        sendRedirect(request, response, successRedirectBaseUrl);
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String uriString = UriComponentsBuilder.newInstance()
                .uri(URI.create(url))
                .queryParam("access_token", request.getAttribute(ACCESS_TOKEN_REQUEST_ATTRIBUTE_NAME))
                .queryParam("refresh_token", request.getAttribute(REFRESH_TOKEN_REQUEST_ATTRIBUTE_NAME))
                .build().toUriString();

        redirectStrategy.sendRedirect(request, response, uriString);

    }

}
