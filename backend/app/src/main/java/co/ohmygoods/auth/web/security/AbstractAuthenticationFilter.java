package co.ohmygoods.auth.web.security;

import co.ohmygoods.auth.jwt.service.AuthenticatedUser;
import co.ohmygoods.auth.jwt.service.JwtAuthenticationToken;
import co.ohmygoods.auth.jwt.service.dto.JwtValidationResult;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())) {
            Authentication authentication = attemptAuthentication(request, response, filterChain);
            saveAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected abstract Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;
}
