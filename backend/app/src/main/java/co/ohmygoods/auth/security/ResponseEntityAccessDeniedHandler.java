package co.ohmygoods.auth.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Component
public class ResponseEntityAccessDeniedHandler extends AbstractSecurityExceptionHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseEntity<?> accessDeniedResponse = createSecurityExceptionResponse(
                accessDeniedException, HttpStatus.FORBIDDEN, URI.create(request.getServletPath()));

        sendSecurityExceptionResponse(response, accessDeniedResponse);
    }
}
