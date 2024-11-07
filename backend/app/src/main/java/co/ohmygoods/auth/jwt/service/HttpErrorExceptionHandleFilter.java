package co.ohmygoods.auth.jwt.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class HttpErrorExceptionHandleFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (HttpClientErrorException e) {
            response.setStatus(e.getStatusCode().value());

            if (e.getResponseHeaders() != null) {
                e.getResponseHeaders().forEach((header, values) -> {
                    response.setHeader(header, String.join(",", values));
                });
            }

            response.getWriter().write(e.getResponseBodyAsString());
        }
    }
}
