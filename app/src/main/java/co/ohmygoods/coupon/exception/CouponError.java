package co.ohmygoods.coupon.exception;

import co.ohmygoods.global.exception.DomainError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CouponError implements DomainError {

    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "C200", "쿠폰을 찾을 수 없습니다."),
    COUPON_ISSUANCE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "C201", "쿠폰 발급 내역을 찾을 수 없습니다."),

    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "C210", "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "C211", "만료된 쿠폰입니다."),
    EXCEED_COUPON_ISSUABLE_LIMIT(HttpStatus.BAD_REQUEST, "C212", "발급 가능한 쿠폰 한도를 초과했습니다."),
    EXHAUSTED_COUPON(HttpStatus.BAD_REQUEST, "C213", "발급 가능한 쿠폰이 모두 소진되었습니다."),

    INVALID_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "C220", "쿠폰 발급에 필요한 필수 입력값이 잘못되었습니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    @Override
    public HttpStatus getHttpStatus() {
        return null;
    }

    @Override
    public String getErrorCode() {
        return "";
    }

    @Override
    public String getErrorMessage() {
        return "";
    }
}
