package co.ohmygoods.global.idempotency.exception;

import co.ohmygoods.global.exception.DomainException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;

public class IdempotencyException extends DomainException {

    public static final IdempotencyException NOT_FOUND_IDEMPOTENCY = new IdempotencyException(IdempotencyError.NOT_FOUND_IDEMPOTENCY);
    public static final IdempotencyException NOT_FOUND_IDEMPOTENCY_HEADER = new IdempotencyException(IdempotencyError.NOT_FOUND_IDEMPOTENCY_HEADER);
    public static final IdempotencyException ALREADY_PROCESS_IDEMPOTENCY_REQUEST = new IdempotencyException(IdempotencyError.ALREADY_PROCESS_IDEMPOTENCY_REQUEST);
    public static final IdempotencyException EMPTY_CACHE_VALUE = new IdempotencyException(IdempotencyError.EMPTY_CACHE_VALUE);

    public static final IdempotencyException FAILED_PARSE_IDEMPOTENCY_REQUEST = new IdempotencyException(IdempotencyError.FAILED_PARSE_IDEMPOTENCY_REQUEST);
    public static final IdempotencyException FAILED_PARSE_IDEMPOTENCY_RESPONSE = new IdempotencyException(IdempotencyError.FAILED_PARSE_IDEMPOTENCY_RESPONSE);

    public IdempotencyException(IdempotencyError error) {
        super(error);
    }

    public IdempotencyException(IdempotencyError error, URI type, URI instance) {
        super(error, type, instance);
    }

    public static IdempotencyException emptyIdempotencyHeader() {
        return NOT_FOUND_IDEMPOTENCY_HEADER;
    }

    public static IdempotencyException emptyCacheValue() {
        return EMPTY_CACHE_VALUE;
    }

}
