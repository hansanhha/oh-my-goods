package co.ohmygoods.product.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ProductError implements DomainError {

    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "P002", "상품 카테고리를 찾을 수 없습니다."),

    CANNOT_UPDATE_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "P100", "상품 상태를 변경할 수 없습니다"),

    INVALID_PURCHASE_QUANTITY(HttpStatus.BAD_REQUEST, "P200", "유효하지 않은 구매 수량입니다"),
    INVALID_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "P201", "유효하지 않은 상품 수량입니다"),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "P202", "유효하지 않은 상품 상태입니다"),
    INVALID_METADATA(HttpStatus.BAD_REQUEST, "P203", "유효하지 않은 상품 정보입니다"),

    NOT_SALES_STATUS(HttpStatus.BAD_REQUEST, "P300", "판매 중이 아닌 상품입니다"),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "P301", "상품 재고가 부족합니다"),
    EXCEED_PURCHASE_PRODUCT_MAX_LIMIT(HttpStatus.BAD_REQUEST, "P302", "상품의 최대 구매 가능 수량을 초과했습니다"),

    DUPLICATE_CUSTOM_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "P401", "중복된 사용자 정의 카테고리 이름입니다"),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "P402", "유효하지 않은 상품 가격입니다"),
    INVALID_SUB_CATEGORY(HttpStatus.BAD_REQUEST, "P403", "유효하지 않은 하위 카테고리입니다");


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

