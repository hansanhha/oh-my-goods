package co.ohmygoods.shop.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ShopError implements DomainError {

    NOT_FOUND_SHOP(HttpStatus.NOT_FOUND, "S001", "상점을 찾을 수 없습니다."),

    INVALID_SHOP_NAME(HttpStatus.BAD_REQUEST, "S100", "유효하지 않은 상점 이름입니다"),
    INVALID_SHOP_STATUS(HttpStatus.BAD_REQUEST, "S101", "유효하지 않은 상점 상태입니다"),
    INVALID_SHOP_OWNER(HttpStatus.FORBIDDEN, "S102", "상점 소유자가 아닙니다");


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
