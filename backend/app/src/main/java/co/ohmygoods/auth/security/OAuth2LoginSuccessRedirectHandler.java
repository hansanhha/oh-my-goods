package co.ohmygoods.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

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
        String uriString = UriComponentsBuilder.fromUriString(successRedirectBaseUrl)
                .queryParam("access_token", request.getAttribute("access_token"))
                .queryParam("refresh_token", request.getAttribute("refresh_token"))
                .build().toUriString();

        redirectStrategy.sendRedirect(request, response, uriString);
    }
}
