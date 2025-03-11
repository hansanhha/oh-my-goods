package co.ohmygoods.cart.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CartError implements DomainError {

    NOT_FOUND_CART(HttpStatus.NOT_FOUND, "C000", "상품을 찾을 수 없습니다."),
    ALREADY_EXIST_PRODUCT(HttpStatus.BAD_REQUEST, "C001", "이미 장바구니에 담긴 상품입니다."),
    EXCEED_CART_MAX_LIMIT(HttpStatus.BAD_REQUEST, "C003", "장바구니가 가득 찼습니다."),
    EXCEED_PRODUCT_MAX_LIMIT(HttpStatus.BAD_REQUEST, "C004", "장바구니에 담을 수 있는 상품의 최대 수량을 초과했습니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 수량입니다.");

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
