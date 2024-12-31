package co.ohmygoods.auth.web.security;

import co.ohmygoods.auth.exception.JwtAuthenticationException;
import co.ohmygoods.global.exception.ExceptionResponseHttpHeaders;
import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder;
import co.ohmygoods.global.exception.ProblemDetailResponseEntityBuilder.ProblemDetailInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * <p>
 * AuthenticationException, AccessDeniedException 발생 시 "application/problem+json" 타입의 예외 응답을 생성/전송하는 유틸 클래스
 * </p>
 * {@link ProblemDetailResponseEntityBuilder}
 * {@link ResponseEntityAuthenticationEntryPoint}
 * {@link ResponseEntityAccessDeniedHandler}
 */
public abstract class AbstractSecurityExceptionHandler {

    protected ResponseEntity<?> buildSecurityExceptionResponse(Exception e, HttpStatus httpStatus, @Nullable URI servletPath) {
        if (e instanceof AuthenticationException authException) {
            return buildAuthenticationExceptionResponse(authException, httpStatus, servletPath);
        }

        return buildAccessDeniedExceptionResponse((AccessDeniedException) e, httpStatus, servletPath);
    }

    protected void sendSecurityExceptionResponse(HttpServletResponse response, ResponseEntity<?> responseEntity) throws IOException {
        response.setStatus(responseEntity.getStatusCode().value());

        responseEntity.getHeaders().forEach((name, values) -> {
            for (String value : values) {
                response.addHeader(name, value);
            }
        });

        Object body = responseEntity.getBody();

        if (Objects.nonNull(body)) {
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            response.getWriter().write(body.toString());
            response.getWriter().flush();
        }
    }

    private ResponseEntity<?> buildAuthenticationExceptionResponse(AuthenticationException e, HttpStatus httpStatus, @Nullable URI servletPath) {
        ProblemDetailInfo problemDetailInfo;

        ProblemDetailInfo.ProblemDetailInfoBuilder problemDetailInfoBuilder = ProblemDetailInfo.builder()
                .httpHeaders(ExceptionResponseHttpHeaders.get())
                .httpStatusCode(httpStatus)
                .title(httpStatus.getReasonPhrase());

        if (Objects.nonNull(servletPath)) {
            problemDetailInfoBuilder.type(servletPath);
        }

        if (e instanceof JwtAuthenticationException jwtException) {
            problemDetailInfo = problemDetailInfoBuilder
                    .exception(jwtException)
                    .detail(jwtException.getError().getMessage())
                    .build();
        } else {
            problemDetailInfo = problemDetailInfoBuilder
                    .exception(e)
                    .build();
        }

        return ProblemDetailResponseEntityBuilder.build(problemDetailInfo);
    }

    private ResponseEntity<?> buildAccessDeniedExceptionResponse(AccessDeniedException e, HttpStatus httpStatus, @Nullable URI servletPath) {
        ProblemDetailInfo.ProblemDetailInfoBuilder problemDetailInfoBuilder = ProblemDetailInfo.builder()
                .httpHeaders(ExceptionResponseHttpHeaders.get())
                .httpStatusCode(httpStatus)
                .title(httpStatus.getReasonPhrase())
                .exception(e);

        if (Objects.nonNull(servletPath)) {
            problemDetailInfoBuilder.type(servletPath);
        }

        ProblemDetailInfo problemDetailInfo = problemDetailInfoBuilder.build();

        return ProblemDetailResponseEntityBuilder.build(problemDetailInfo);
    }

}
