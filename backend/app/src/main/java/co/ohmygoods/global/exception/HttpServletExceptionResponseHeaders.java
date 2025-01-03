package co.ohmygoods.global.exception;

import co.ohmygoods.auth.security.config.SecurityConfigProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * HttpServletResponse를 통해 응답을 보내는 경우 http 헤더를 설정하기 위한 클래스
 */
@Component
@RequiredArgsConstructor
public class HttpServletExceptionResponseHeaders {

    private static final String COMMA = ",";

    private final SecurityConfigProperties.CorsProperties corsProperties;

    @Setter
    private HttpHeaders httpHeaders;

    @PostConstruct
    void init() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.add(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue());
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, String.join(COMMA, corsProperties.getAccessControlAllowOrigin()));
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(COMMA, corsProperties.getAccessControlAllowHeaders()));
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(COMMA, corsProperties.getAccessControlAllowMethods()));
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(corsProperties.isAccessControlAllowCredentials()));
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, String.join(COMMA, corsProperties.getAccessControlExposeHeaders()));
        httpHeaders.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, CacheControl.maxAge(Duration.ZERO).getHeaderValue());
        this.httpHeaders = httpHeaders;
    }

    public HttpHeaders get() {
        return httpHeaders;
    }
}
