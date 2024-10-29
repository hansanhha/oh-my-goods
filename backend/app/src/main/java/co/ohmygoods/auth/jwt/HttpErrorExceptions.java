package co.ohmygoods.auth.jwt;

import co.ohmygoods.auth.config.SecurityConfigProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HttpErrorExceptions {

    private static final String COMMA = ",";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    HttpClientErrorException unauthorized(Map<?, ?> body) {
        return HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.getReasonPhrase(), httpHeaders, convertBody(body), DEFAULT_CHARSET);
    }

    HttpClientErrorException forbidden(Map<?, ?> body) {
        return HttpClientErrorException.create(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), httpHeaders, convertBody(body), DEFAULT_CHARSET);
    }

    private byte[] convertBody(Map<?, ?> body) {
        byte[] message = null;

        try {
            message = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException ignored) {}

        return message;
    }
}