package co.ohmygoods.global.idempotency.service;

import co.ohmygoods.global.idempotency.service.dto.IdempotencyRequest;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyCacheKeyGenerator {

    private static final String IDEMPOTENCY_CACHE_KEY_TEMPLATE = "idempotency:%s:%s:%s:%s";

    public String generate(IdempotencyRequest request) {
        return String.format(IDEMPOTENCY_CACHE_KEY_TEMPLATE,
                request.idempotencyKey(), request.httpMethod(), request.servletPath(), request.accessToken());
    }
}
