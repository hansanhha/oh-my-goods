package co.ohmygoods.auth.web.security;

import co.ohmygoods.auth.web.security.config.SecurityConfigProperties;
import co.ohmygoods.auth.jwt.service.HttpErrorExceptions;
import co.ohmygoods.auth.jwt.service.JWTAuthenticationToken;
import co.ohmygoods.auth.jwt.service.JwtService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JWTBearerAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityConfigProperties.SignUrlProperties signUrlProperties;
    private final SecurityConfigProperties.WhitelistProperties whitelistProperties;
    private final HttpErrorExceptions httpErrorExceptions;
    private final List<RequestMatcher> oauth2ProcessingRequestMatchers;

    @PostConstruct
    void init() {
        oauth2ProcessingRequestMatchers.add(new AntPathRequestMatcher(signUrlProperties.getOauth2AuthorizationProcessingUrl()));
        oauth2ProcessingRequestMatchers.add(new AntPathRequestMatcher(signUrlProperties.getOauth2LoginProcessingUrl()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var optionalBearerToken = extractBearerToken(request);

        if (optionalBearerToken.isEmpty()) {
            throw httpErrorExceptions.unauthorized(Map.of("message", "invalid credentials"));
        }

        var bearerToken = optionalBearerToken.get();
        var validationResult = jwtService.validateAccessToken(bearerToken);

        if (validationResult.isValid()) {
            throw httpErrorExceptions.unauthorized(Map.of("message", validationResult.invalid().getDescription()));
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
        return
                whitelistProperties
                        .getWhiteServletPathList()
                        .stream()
                        .anyMatch(servletPath -> servletPath.equals(request.getServletPath())) ||
                oauth2ProcessingRequestMatchers
                        .stream()
                        .anyMatch(matcher -> matcher.matches(request));
    }

}
