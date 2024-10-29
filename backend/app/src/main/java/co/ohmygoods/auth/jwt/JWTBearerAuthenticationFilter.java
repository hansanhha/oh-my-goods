package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.config.SecurityConfigProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTBearerAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final SecurityConfigProperties.SignUrlProperties signUrlProperties;
    private final SecurityConfigProperties.WhitelistProperties whitelistProperties;
    private final HttpErrorExceptions httpErrorExceptions;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var optionalBearerToken = extractBearerToken(request);

        if (optionalBearerToken.isEmpty()) {
            throw httpErrorExceptions.unauthorized(Map.of("message","invalid credentials"));
        }

        var bearerToken = optionalBearerToken.get();
        var validationResult = jwtService.validateToken(bearerToken);

        if (validationResult.hasError()) {
            throw httpErrorExceptions.unauthorized(Map.of("message",validationResult.error().getDescription()));
        }

        var jwtAuthenticationToken = JWTAuthenticationToken.authenticated(validationResult.jwtInfo(), null);

        SecurityContextHolder.clearContext();
        var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(jwtAuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractBearerToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authorizationHeader.replace("Bearer ", ""));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        var servletPath = request.getServletPath();
        return whitelistProperties.getWhiteServletPathList()
                .stream()
                .anyMatch(servletPath::equals) ||
                servletPath.startsWith(signUrlProperties.getOauth2AuthorizationBaseUrl()) ||
                servletPath.startsWith(signUrlProperties.getOauth2LoginProcessingUrl());
    }

}
