package co.ohmygoods.global.idempotency.service;

import co.ohmygoods.global.idempotency.service.dto.IdempotencyRequest;
import co.ohmygoods.global.idempotency.service.dto.IdempotencyResponse;
import co.ohmygoods.global.idempotency.entity.Idempotency;
import co.ohmygoods.global.idempotency.exception.IdempotencyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    public static final String IDEMPOTENCY_HEADER_NAME = "Idempotency-Key";

    private final RedisTemplate<String, Idempotency> redisTemplate;
    private final IdempotencyCacheKeyGenerator cacheKeyGenerator;

    public void cacheRequest(IdempotencyRequest request) {
        if (isCached(request)) {
            throw IdempotencyException.ALREADY_PROCESS_IDEMPOTENCY_REQUEST;
        }

        Idempotency idempotency = Idempotency.create(cacheKeyGenerator.generate(request));
        redisTemplate.opsForValue().set(idempotency.getId(), idempotency);
    }

    public void cacheResponse(IdempotencyRequest request, IdempotencyResponse response) {
        Idempotency idempotency = get(request);

        if (idempotency.isProcessed()) {
            throw IdempotencyException.ALREADY_PROCESS_IDEMPOTENCY_REQUEST;
        }

        idempotency.cacheResponse(response.httpStatusCode().value(), response.responseBody());
        redisTemplate.opsForValue().set(idempotency.getId(), idempotency);
    }

    public IdempotencyResponse getCachedResponse(IdempotencyRequest request) {
        Idempotency idempotency = get(request);

        return new IdempotencyResponse(HttpStatusCode.valueOf(idempotency.getResponseStatus()), idempotency.getResponseBody());
    }

    public boolean isProcessing(IdempotencyRequest request) {
        if (!isCached(request)) {
            return false;
        }

        return get(request).isProcessing();
    }

    public boolean isCached(IdempotencyRequest request) {
        return redisTemplate.hasKey(cacheKeyGenerator.generate(request));
    }

    private Idempotency get(IdempotencyRequest request) {
        if (!isCached(request)) {
            throw IdempotencyException.NOT_FOUND_IDEMPOTENCY;
        }

        Optional<Idempotency> idempotency = Optional.ofNullable(redisTemplate.opsForValue().get(cacheKeyGenerator.generate(request)));

        return idempotency.orElseThrow(IdempotencyException::emptyCacheValue);
    }

}
