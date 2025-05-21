package co.ohmygoods.shop.exception;


import co.ohmygoods.global.exception.DomainError;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public enum ShopError implements DomainError {

    NOT_FOUND_SHOP(HttpStatus.NOT_FOUND, "S001", "상점을 찾을 수 없습니다."),

    NOT_FOUND_SELLER_PRODUCT(HttpStatus.NOT_FOUND, "S002", "상품을 찾을 수 없습니다."),

    ALREADY_EXIST_SHOP(HttpStatus.BAD_REQUEST, "S100", "이미 존재하는 상점입니다"),
    ALREADY_EXIST_SHOP_OWNER(HttpStatus.BAD_REQUEST, "S101", "이미 상점을 소유하고 있는 사용자입니다");

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
