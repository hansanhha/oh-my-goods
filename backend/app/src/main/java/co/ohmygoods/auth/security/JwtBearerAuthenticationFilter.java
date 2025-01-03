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
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtBearerAuthenticationFilter extends AbstractAuthenticationFilter {

    private static final String BEARER = "Bearer ";

    private final JwtValidator jwtValidator;
    private final List<RequestMatcher> permitRequests;
    private final Converter<JwtValidationResult, JwtAuthenticationToken> jwtValidationResultConverter = this::createJwtAuthenticationToken;

    @Override
    protected Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = extractBearerToken(request);

        JwtValidationResult validationResult = jwtValidator.validate(jwt);

        if (!validationResult.isValid()) {
            throw AuthException.of(validationResult.error());
        }

        return jwtValidationResultConverter.convert(validationResult);
    }

    private String extractBearerToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER)) {
            throw AuthException.EMPTY_BEARER_HEADER;
        }

        return authorizationHeader.replace(BEARER, "");
    }

    private JwtAuthenticationToken createJwtAuthenticationToken(JwtValidationResult validationResult) {
        return new JwtAuthenticationToken(new AuthenticatedAccount(validationResult.getSubject(), validationResult.getRole()));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return permitRequests.stream().anyMatch(ignore -> ignore.matches(request));
    }
}
