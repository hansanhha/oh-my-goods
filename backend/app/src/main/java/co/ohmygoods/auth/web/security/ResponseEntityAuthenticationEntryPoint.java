package co.ohmygoods.auth.web.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class ResponseEntityAuthenticationEntryPoint extends AbstractSecurityExceptionHandler
        implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<?> authenticationExceptionResponse = createSecurityExceptionResponse(
                authException, HttpStatus.UNAUTHORIZED, URI.create(request.getServletPath()));

        sendSecurityExceptionResponse(response, authenticationExceptionResponse);
    }
}
