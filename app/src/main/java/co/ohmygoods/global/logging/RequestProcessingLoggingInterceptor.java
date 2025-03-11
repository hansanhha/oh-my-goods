package co.ohmygoods.global.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * <p>요청-응답 처리 로깅 (request uri, http method, queryString, response status, 소요 시간)</p>
 * <p>요청 식별 uuid를 MDC에 삽입(로거가 내부적으로 사용)</p>
 * <p>전반적인 요청-응답 과정의 메타 정보만 INFO 레벨에서 로깅하고 예외 로깅은 하지 않는다</p>
 * 예외 로깅: {@link co.ohmygoods.global.exception.GlobalExceptionHandler}
 */
@Component
@Slf4j
public class RequestProcessingLoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_TRACE_ID = "requestTraceId";
    private static final String REQUEST_START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MDC.put(REQUEST_TRACE_ID, getRequestTraceId());
        MDC.put(REQUEST_START_TIME, String.valueOf(System.currentTimeMillis()));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        long requestStartTime = Long.parseLong(MDC.get(REQUEST_START_TIME));
        MDC.remove(REQUEST_START_TIME);
        long duration = System.currentTimeMillis() - requestStartTime;

        log.info("processed request. httpMethod: {} uri: {} queryString: {} responseStatus: {} duration: {}",
                request.getMethod(), request.getRequestURI(), request.getQueryString(),
                HttpStatus.valueOf(response.getStatus()), duration);
    }

    private String getRequestTraceId() {
        return UUID.randomUUID().toString();
    }
}
