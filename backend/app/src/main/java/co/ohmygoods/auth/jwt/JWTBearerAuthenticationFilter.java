package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.exception.UnauthorizedException;
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
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTBearerAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final List<String> whiteServeltPathList;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var optionalBearerToken = extractBearerToken(request);

        if (optionalBearerToken.isEmpty()) {
            throw new UnauthorizedException();
        }

        var bearerToken = optionalBearerToken.get();
        var validationResult = jwtService.validateToken(bearerToken);

        if (validationResult.hasError()) {
            throw new UnauthorizedException(validationResult.error().getDescription());
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
        return whiteServeltPathList
                .stream()
                .anyMatch(path -> request.getServletPath().equals(path));
    }
}
