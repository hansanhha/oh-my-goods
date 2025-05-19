package co.ohmygoods.auth.security;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.auth.jwt.service.JWTValidator;
import co.ohmygoods.auth.jwt.service.dto.JWTValidationResult;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Component
@RequiredArgsConstructor
public class JwtBearerAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final PermitRequestMatcher permitRequestsMatcher;
    private final JWTValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Authentication authentication = attemptAuthentication(request, response, filterChain);
            saveAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return permitRequestsMatcher.matches(request);
    }

    private Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = extractBearerToken(request);

        JWTValidationResult validationResult = jwtValidator.validate(bearerToken);

        if (!validationResult.isValid()) {
            throw validationResult.error();
        }

        return createJwtAuthenticationToken(validationResult, bearerToken);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader.isBlank() || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw AuthException.EMPTY_BEARER_HEADER;
        }

        return authorizationHeader.replace(BEARER_PREFIX, "");
    }

    private JWTAuthenticationToken createJwtAuthenticationToken(JWTValidationResult validationResult, String bearerToken) {
        return new JWTAuthenticationToken(new AuthenticatedAccount(bearerToken, validationResult.getSubClaim(), validationResult.getRoleClaim()));
    }

    private void saveAuthentication(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

}
