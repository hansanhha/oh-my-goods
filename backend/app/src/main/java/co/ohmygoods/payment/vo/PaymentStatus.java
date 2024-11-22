package co.ohmygoods.payment.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    PAYING("결제 중"),
    PAYMENT_CANCEL("결제 취소"),
    PAYMENT_FAILED_TIMEOUT("결제 실패(시간 초과)"),
    PAYMENT_FAILED_INSUFFICIENT_BALANCE("결제 실패(잔액 부족)"),
    PAYMENT_FAILED_BANK_CHECK_TIME("결제 실패(은행 점검 시간)"),
    PAYMENT_FAILED_CARD_LIMIT_EXCEEDED("결제 실패(카드 한도 초과)"),
    PAYMENT_FAILED_INVALID_CARD_INFO("결제 실패(잘못된 카드 정보 또는 카드 유효기간 말소)"),
    PAYMENT_FAILED_NETWORK_ERROR("결제 실패(네트워크 오류)"),
    PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR("결제 실패(외부 환경 오류)"),
    PAID("결제 성공");

    private final String message;
}
