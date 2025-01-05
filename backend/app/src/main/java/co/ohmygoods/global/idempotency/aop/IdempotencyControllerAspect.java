package co.ohmygoods.global.idempotency.aop;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.global.exception.DomainException;
import co.ohmygoods.global.idempotency.service.dto.IdempotencyRequest;
import co.ohmygoods.global.idempotency.service.dto.IdempotencyResponse;
import co.ohmygoods.global.idempotency.exception.IdempotencyException;
import co.ohmygoods.global.idempotency.service.IdempotencyService;

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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static co.ohmygoods.global.idempotency.service.IdempotencyService.IDEMPOTENCY_HEADER_NAME;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyControllerAspect {

    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("@annotation(org.springframework.stereotype.Controller) && " +
            "@annotation(co.ohmygoods.global.idempotency.aop.Idempotent)")
    public Object processIdempotency(ProceedingJoinPoint controller) throws Throwable {
        IdempotencyRequest request = getIdempotencyRequest(controller);

        if (idempotencyService.isCached(request)) {
            if (idempotencyService.isProcessing(request)) {
                throw IdempotencyException.ALREADY_PROCESS_IDEMPOTENCY_REQUEST;
            }

            IdempotencyResponse cached = idempotencyService.getCachedResponse(request);
            return ResponseEntity.status(cached.httpStatusCode()).body(cached.responseBody());
        }

        idempotencyService.cacheRequest(request);

        try {
            Object controllerResponse = controller.proceed(controller.getArgs());

            IdempotencyResponse idempotencyResponse = convertIdempotencyResponse(controllerResponse);
            idempotencyService.cacheResponse(request, idempotencyResponse);

            return controllerResponse;
        } catch (Throwable e) {
            if (e instanceof DomainException domainEx) {
                HttpStatus errorStatus = domainEx.getHttpStatus();
                String errorDetailMessage = domainEx.getErrorDetailMessage();

                IdempotencyResponse idempotencyResponse = new IdempotencyResponse(errorStatus, errorDetailMessage);
                idempotencyService.cacheResponse(request, idempotencyResponse);
            }

            throw e;
        }
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

    private IdempotencyRequest getIdempotencyRequest(JoinPoint joinPoint) {
        ExtractingIdempotencyInfo info = new ExtractingIdempotencyInfo();

        extractRequireKey(info, joinPoint);
        extractHttpInfo(info, joinPoint);

        info.validate();

        return new IdempotencyRequest(info.idempotencyKey, info.httpMethod, info.servletPath, info.accessToken);
    }

    private void extractRequireKey(ExtractingIdempotencyInfo info, JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] params = method.getParameters();

        for (int i = 0; i < params.length; i++) {
            Parameter param = params[i];

            if (param.isAnnotationPresent(RequestHeader.class) &&
                    param.getAnnotation(RequestHeader.class).value().equals(IDEMPOTENCY_HEADER_NAME)) {
                info.setIdempotencyKey((String) joinPoint.getArgs()[i]);
            }

            if (param.isAnnotationPresent(AuthenticationPrincipal.class) &&
                    param.getType().equals(AuthenticatedAccount.class)) {
                AuthenticatedAccount principal = (AuthenticatedAccount) joinPoint.getArgs()[i];
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
    @RequiredArgsConstructor
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
