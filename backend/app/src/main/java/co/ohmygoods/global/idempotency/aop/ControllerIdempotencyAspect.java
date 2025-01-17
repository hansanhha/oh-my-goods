package co.ohmygoods.global.idempotency.aop;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.global.exception.DomainException;
import co.ohmygoods.global.idempotency.IdempotencyLockProperties;
import co.ohmygoods.global.idempotency.vo.Idempotency;
import co.ohmygoods.global.idempotency.aop.dto.IdempotencyRequest;
import co.ohmygoods.global.idempotency.aop.dto.IdempotencyResponse;
import co.ohmygoods.global.idempotency.exception.IdempotencyException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Aspect
@Component
@RequiredArgsConstructor
public class ControllerIdempotencyAspect {

    private static final String IDEMPOTENCY_CACHE_PREFIX = "idempotency:";

    private final RedissonClient redissonClient;
    private final IdempotencyLockProperties lockProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(org.springframework.stereotype.Controller) && " +
            "@annotation(co.ohmygoods.global.idempotency.aop.Idempotent)")
    public Object processIdempotency(ProceedingJoinPoint controller) throws Throwable {
        IdempotencyRequest request = extractIdempotencyRequest(controller);

        RMap<String, Idempotency> idempotencyCache = redissonClient.getMap(IDEMPOTENCY_CACHE_PREFIX);
        String key = generateCacheKey(request);

        Idempotency cached = idempotencyCache.get(key);

        // http 요청이 이미 캐시된 경우
        // - 처리하는 중: 예외 발생
        // - 처리된 요청: 캐시된 응답값 반환
        if (cached != null) {
            if (cached.isProcessing()) {
                throw IdempotencyException.ALREADY_PROCESS_IDEMPOTENCY_REQUEST;
            }

            return ResponseEntity.status(cached.getResponseStatus()).body(cached.getResponseBody());
        }

        // http 요청을 캐시한다
        // 컨트롤러 실행 전, 락을 획득하여 요청 정보를 레디스에 저장한다 (중복 처리 방지)
        // 락 획득에 실패하면 다른 스레드에서 이미 멱등 처리 중인 것으로 간주한다
        // 이후 컨트롤러를 실행하여 받은 응답을 저장하고 (응답 값 캐시) 락을 반납한다
        RLock rLock = redissonClient.getLock(key);
        boolean isGetLock = rLock.tryLock(lockProperties.getWaitTime(), lockProperties.getLeaseTime(), lockProperties.getTimeUnit());

        if (!isGetLock) {
            throw IdempotencyException.ALREADY_PROCESS_IDEMPOTENCY_REQUEST;
        }

        Idempotency idempotency = Idempotency.create(request.idempotencyKey(),
                request.httpMethod(), request.servletPath(), request.accessToken());

        idempotencyCache.put(key, idempotency);

        try {
            Object controllerResponse = controller.proceed(controller.getArgs());

            IdempotencyResponse idempotencyResponse = convertIdempotencyResponse(controllerResponse);
            idempotency.cacheResponse(idempotencyResponse.httpStatusCode().value(), idempotencyResponse.responseBody());
            idempotencyCache.put(key, idempotency);

            return controllerResponse;
        }
        catch (Throwable e) {
            if (e instanceof DomainException domainEx) {
                HttpStatus errorStatus = domainEx.getHttpStatus();
                String errorDetailMessage = domainEx.getErrorDetailMessage();

                IdempotencyResponse idempotencyResponse = new IdempotencyResponse(errorStatus, errorDetailMessage);
                idempotency.cacheResponse(idempotencyResponse.httpStatusCode().value(), idempotencyResponse.responseBody());
                idempotencyCache.put(key, idempotency);
            }
            else {
                idempotency.cacheUnknownError();
            }
            throw e;
        }
        finally {
            try {
                rLock.unlock();
            } catch (IllegalMonitorStateException e) {
                // 이미 락이 해제된 경우
            }
        }
    }

    private String generateCacheKey(IdempotencyRequest request) {
        return request.idempotencyKey() + ":" + request.httpMethod()
                + ":" + request.servletPath() + ":" + request.accessToken();
    }

    private IdempotencyResponse convertIdempotencyResponse(Object response) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            try {
                return new IdempotencyResponse(responseEntity.getStatusCode(),
                        objectMapper.writeValueAsString(responseEntity.getBody()));
            } catch (JsonProcessingException e) {
                throw IdempotencyException.FAILED_PARSE_IDEMPOTENCY_RESPONSE;
            }
        }

        throw IdempotencyException.FAILED_PARSE_IDEMPOTENCY_RESPONSE;
    }

    private IdempotencyRequest extractIdempotencyRequest(JoinPoint controller) {
        ExtractingIdempotencyInfo info = new ExtractingIdempotencyInfo();

        extractRequireKey(info, controller);
        extractHttpInfo(info, controller);

        info.validate();

        return new IdempotencyRequest(info.idempotencyKey, info.httpMethod, info.servletPath, info.accessToken);
    }

    private void extractRequireKey(ExtractingIdempotencyInfo info, JoinPoint controller) {
        MethodSignature signature = (MethodSignature) controller.getSignature();
        Method method = signature.getMethod();
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (param.isAnnotationPresent(RequestHeader.class) &&
                    param.getAnnotation(RequestHeader.class).value().equals(IDEMPOTENCY_HEADER)) {
                info.setIdempotencyKey((String) controller.getArgs()[i]);
            }

            if (param.isAnnotationPresent(AuthenticationPrincipal.class) &&
                    param.getType().equals(AuthenticatedAccount.class)) {
                AuthenticatedAccount principal = (AuthenticatedAccount) controller.getArgs()[i];
                info.setAccessToken(principal.jwt());
            }
        }
    }

    private void extractHttpInfo(ExtractingIdempotencyInfo info, JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = joinPoint.getClass().getAnnotation(RequestMapping.class);
            info.setHttpMethod(requestMapping.method()[0].name());
        }

        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = joinPoint.getClass().getAnnotation(RequestMapping.class);
            info.setServletPath(requestMapping.value()[0]);
        }
    }

    @NoArgsConstructor
    @Setter
    private static class ExtractingIdempotencyInfo {
        private String idempotencyKey;
        private String httpMethod;
        private String servletPath;
        private String accessToken;

        void validate() {
            if (idempotencyKey == null) {
                throw IdempotencyException.NOT_FOUND_IDEMPOTENCY_HEADER;
            }

            if (httpMethod == null || servletPath == null || accessToken == null) {
                throw IdempotencyException.FAILED_PARSE_IDEMPOTENCY_REQUEST;
            }
        }
    }
}
