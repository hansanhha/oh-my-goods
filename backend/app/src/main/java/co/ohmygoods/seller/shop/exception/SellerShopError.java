package co.ohmygoods.seller.shop.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SellerShopError implements DomainError {

    NOT_FOUND_SHOP(HttpStatus.NOT_FOUND, "SS000", "상점을 찾을 수 없습니다"),
    INVALID_SHOP_CREATION_INFO(HttpStatus.BAD_REQUEST, "SS001", "상점 생성 필수 정보가 유효하지 않습니다"),
    ALREADY_EXIST_SHOP(HttpStatus.BAD_REQUEST, "SS002", "이미 존재하는 상점입니다"),
    ALREADY_EXIST_SHOP_OWNER(HttpStatus.BAD_REQUEST, "SS003", "이미 상점을 소유하고 있는 사용자입니다"),
    INVALID_SHOP_STATUS(HttpStatus.BAD_REQUEST, "SS004", "판매자 상점 상태가 유효하지 않습니다");

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
