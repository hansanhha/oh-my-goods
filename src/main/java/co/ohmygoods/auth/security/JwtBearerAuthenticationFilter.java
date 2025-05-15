package co.ohmygoods.auth.security;

import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.auth.jwt.service.JwtAuthenticationToken;
import co.ohmygoods.auth.jwt.service.JwtValidator;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.io.IOException;

public class JwtBearerAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final String BEARER = "Bearer ";

    public JwtBearerAuthenticationFilter(PermitRequestMatcher permitRequestsMather, JwtValidator jwtValidator) {
        super(permitRequestsMather);
        this.jwtValidator = jwtValidator;
    }

    private final JwtValidator jwtValidator;

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractBearerToken(request);

        JwtValidationResult validationResult = jwtValidator.validate(jwt);

        if (!validationResult.isValid()) {
            throw validationResult.error();
        }

        return createJwtAuthenticationToken(validationResult, jwt);
    }

    private String extractBearerToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw AuthException.EMPTY_BEARER_HEADER;
        }

        return authorizationHeader.replace(BEARER, "");
    }

    private JwtAuthenticationToken createJwtAuthenticationToken(JwtValidationResult validationResult, String jwt) {
        return new JwtAuthenticationToken(new AuthenticatedAccount(jwt, validationResult.getSubject(), validationResult.getRole()));
    }

}
