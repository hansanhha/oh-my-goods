package co.ohmygoods.auth.security;

import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.global.exception.HttpServletExceptionResponseHeaders;
import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder;
import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder.ProblemDetailInfo;
import com.esotericsoftware.kryo.util.ObjectMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * 필터 계층에서 AuthException, AuthenticationException, AccessDeniedException 발생 시
 * "application/problem+json" 타입의 예외 응답을 생성/전송하는 유틸 클래스
 * </p>
 * {@link ProblemDetailResponseEntityBuilder}
 */
@Component
@RequiredArgsConstructor
public class SecurityExceptionProcessingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpServletExceptionResponseHeaders httpServletExceptionResponseHeaders;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        }
        catch (AuthException e) {
            processAuthException(e, request, response);
        }
        catch (AuthenticationException | AccessDeniedException e) {
            processSpringSecurityException(e, request, response);
        }
    }

    private void processAuthException(AuthException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseEntity<?> jwtExceptionResponse = buildExceptionResponse(e, e.getHttpStatus(),
                e.getErrorMessage(), e.getErrorDetailMessage(), e.getType(), e.getInstance());
        sendSecurityExceptionResponse(response, jwtExceptionResponse);
    }

    private void processSpringSecurityException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ResponseEntity<?> responseEntity;

        if (e instanceof AuthenticationException authException) {
            responseEntity = buildExceptionResponse(authException, HttpStatus.UNAUTHORIZED,
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(), null, URI.create(request.getServletPath()), URI.create(request.getRequestURI()));
        } else {
            responseEntity =  buildExceptionResponse(e, HttpStatus.FORBIDDEN,
                    HttpStatus.FORBIDDEN.getReasonPhrase(), null, URI.create(request.getServletPath()), URI.create(request.getRequestURI()));
        }

        sendSecurityExceptionResponse(response, responseEntity);
    }

    private ResponseEntity<ProblemDetail> buildExceptionResponse(Exception e, HttpStatus httpStatus,
                                                                 @Nullable String title, @Nullable String detail,
                                                                 @Nullable URI type, @Nullable URI instance) {

        ProblemDetailInfo problemDetailInfo = ProblemDetailInfo.builder()
                .httpHeaders(httpServletExceptionResponseHeaders.get())
                .httpStatusCode(httpStatus)
                .title(StringUtils.hasText(title) ? title : httpStatus.getReasonPhrase())
                .detail(StringUtils.hasText(detail) ? detail : e.getMessage())
                .type(Optional.ofNullable(type).orElse(URI.create("about:blank")))
                .instance(Optional.ofNullable(instance).orElse(URI.create("about:blank")))
                .build();

        return ProblemDetailResponseEntityBuilder.build(problemDetailInfo);
    }

    private void sendSecurityExceptionResponse(HttpServletResponse response, ResponseEntity<?> responseEntity) throws IOException {
        response.setStatus(responseEntity.getStatusCode().value());

        responseEntity.getHeaders().forEach((name, values) ->
                values.forEach(value -> response.addHeader(name, value)));

        Object responseEntityBody = responseEntity.getBody();

        if (Objects.nonNull(responseEntityBody)) {
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            String responseBody = objectMapper.writeValueAsString(responseEntityBody);
            response.getWriter().write(responseBody);
            response.getWriter().flush();
        }
    }

}
