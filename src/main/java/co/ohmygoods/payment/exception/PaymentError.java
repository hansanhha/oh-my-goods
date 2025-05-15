package co.ohmygoods.payment.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum PaymentError implements DomainError {

    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "P001", "결제 내역을 찾을 수 없습니다."),

    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "P100", "유효하지 않은 결제 수단입니다."),
    INVALID_PURCHASE_AMOUNT(HttpStatus.BAD_REQUEST, "P101", "유효하지 않은 결제 금액입니다."),
    CANNOT_UPDATE_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "P102", "결제 수단을 변경할 수 없습니다."),
    NOT_SUPPORTED_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "P103", "지원하지 않는 결제 수단입니다."),

    FAILED_PAYMENT_API_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "P200", "결제에 실패했습니다(서버 오류).");

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
