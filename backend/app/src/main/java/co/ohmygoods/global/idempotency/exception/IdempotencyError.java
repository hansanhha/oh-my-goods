package co.ohmygoods.global.idempotency.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum IdempotencyError implements DomainError {

    NOT_FOUND_IDEMPOTENCY(HttpStatus.NOT_FOUND, "I800", "멱등값을 찾을 수 없습니다."),
    NOT_FOUND_IDEMPOTENCY_HEADER(HttpStatus.BAD_REQUEST, "I801", "멱등 헤더를 찾을 수 없습니다"),
    ALREADY_PROCESS_IDEMPOTENCY_REQUEST(HttpStatus.CONFLICT, "I802", "이미 처리 중이거나 처리된 멱등 요청입니다."),
    EMPTY_CACHE_VALUE(HttpStatus.INTERNAL_SERVER_ERROR, "I803", "멱등 캐시 값이 비어있습니다."),

    FAILED_PARSE_IDEMPOTENCY_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "I500", "멱등 요청을 파싱하는데 실패했습니다."),
    FAILED_PARSE_IDEMPOTENCY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "I501", "멱등 응답을 파싱하는데 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
