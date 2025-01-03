package co.ohmygoods.order.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DeliveryAddressError implements DomainError {

    NOT_FOUND_DELIVERY_ADDRESS(HttpStatus.NOT_FOUND, "D001", "배송지 정보를 찾을 수 없습니다."),
    INVALID_DELIVERY_ADDRESS(HttpStatus.BAD_REQUEST, "D100", "유효하지 않은 배송지 정보입니다."),
    CANNOT_UPDATE_DELIVERY_ADDRESS(HttpStatus.BAD_REQUEST, "D101", "배송지 정보를 변경할 수 없습니다.");

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
