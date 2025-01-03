package co.ohmygoods.seller.product.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SellerProductError implements DomainError {

    NOT_FOUND_SELLER_PRODUCT(HttpStatus.NOT_FOUND, "SP001", "상품을 찾을 수 없습니다."),

    INVALID_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "SP100", "유효하지 않은 상품 수량입니다"),
    INVALID_PRODUCT_STATUS(HttpStatus.BAD_REQUEST, "SP101", "유효하지 않은 상품 상태입니다"),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "SP101", "유효하지 않은 상품 가격입니다"),
    INVALID_SUB_CATEGORY(HttpStatus.BAD_REQUEST, "SP102", "유효하지 않은 하위 카테고리입니다"),

    DUPLICATE_CUSTOM_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "SP200", "중복된 사용자 정의 카테고리 이름입니다");

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
