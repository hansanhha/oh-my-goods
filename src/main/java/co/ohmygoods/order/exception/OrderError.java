package co.ohmygoods.order.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OrderError implements DomainError {

    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "O001", "주문 내역을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_ITEM(HttpStatus.NOT_FOUND, "O002", "주문 상품을 찾을 수 없습니다."),
    NOT_FOUND_EXCHANGE(HttpStatus.NOT_FOUND, "O003", "교환 내역을 찾을 수 없습니다."),
    NOT_FOUND_REFUND(HttpStatus.NOT_FOUND, "O004", "환불 내역을 찾을 수 없습니다."),

    INVALID_PURCHASE_AMOUNT(HttpStatus.BAD_REQUEST, "O100", "유효하지 않은 구매 금액입니다."),
    INVALID_PURCHASE_QUANTITY(HttpStatus.BAD_REQUEST, "O101", "유효하지 않은 구매 수량입니다."),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O102", "유효하지 않은 주문 상태입니다."),
    CANNOT_UPDATE_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O103", "주문 상태를 변경할 수 없습니다."),
    CANNOT_UPDATE_ORDER_ITEM_STATUS(HttpStatus.BAD_REQUEST, "O104", "주문 상품의 상태를 변경할 수 없습니다."),
    CANNOT_UPDATE_DELIVERY_ADDRESS(HttpStatus.BAD_REQUEST, "O105", "배송지를 변경할 수 없습니다"),

    ORDER_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "O200", "이미 완료된 주문입니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "O201", "이미 취소된 주문입니다."),
    ORDER_ALREADY_EXCHANGE_REQUESTED(HttpStatus.BAD_REQUEST, "O202", "이미 교환 요청된 주문입니다."),
    ORDER_ALREADY_EXCHANGED(HttpStatus.BAD_REQUEST, "O203", "이미 교환된 주문입니다."),
    ORDER_ALREADY_REFUND_REQUESTED(HttpStatus.BAD_REQUEST, "O204", "이미 환불 요청된 주문입니다."),
    ORDER_ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "O205", "이미 환불된 주문입니다."),

    INVALID_CS_REQUEST_REASON(HttpStatus.BAD_REQUEST, "O300", "유효하지 않은 CS 요청 사유입니다."),
    INVALID_CS_RESPONSE(HttpStatus.BAD_REQUEST, "O300", "유효하지 않은 CS 답변입니다.");

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
