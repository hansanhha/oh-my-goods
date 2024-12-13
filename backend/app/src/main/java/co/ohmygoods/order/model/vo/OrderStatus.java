package co.ohmygoods.order.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PAYMENT_START("결제 시작"),
    PAYMENT_READY("결제 준비"),
    PAYING("결제 중"),
    PAYMENT_CANCEL("결제 취소"),
    PAYMENT_FAILED_TIMEOUT("결제 실패(시간 초과)"),
    PAYMENT_FAILED_INSUFFICIENT_BALANCE("결제 실패(잔액 부족)"),
    PAYMENT_FAILED_BANK_CHECK_TIME("결제 실패(은행 점검 시간)"),
    PAYMENT_FAILED_CARD_LIMIT_EXCEEDED("결제 실패(카드 한도 초과)"),
    PAYMENT_FAILED_INVALID_CARD_INFO("결제 실패(잘못된 카드 정보 또는 카드 유효기간 말소)"),
    PAYMENT_FAILED_NETWORK_ERROR("결제 실패(네트워크 오류)"),
    PAYMENT_FAILED_OTHER_EXTERNAL_API_ERROR("결제 실패(외부 환경 오류)"),
    PAID("결제 성공"),

    OUT_OF_STOCK("주문 수량 초과"),
    INVALID_ADDRESS("유효하지 않는 배송지"),
    FAILED_OTHER("기타"),

    ORDER_READY("주문 준비"),
    ORDERED("주문 완료"),
    ORDER_FAILED_LACK_QUANTITY("주문 실패(수량 부족)"),
    ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS("주문 실패(판매 상품 아님)"),
    PACKAGING("배송 대기 중"),
    DELIVERING("배송 중"),
    DELIVERED("배송 완료"),
    COMPLETED("구매 확정"),

    CANCELED_ORDER("주문 취소됨"),
    REQUESTED_CANCEL_ORDER("주문 취소 요청됨"),
    REJECTED_CANCEL_ORDER("주문 취소 거절됨"),

    REQUESTED_REFUNDING("환불 요청됨"),
    REJECTED_REFUNDING("환불 거절됨"),
    APPROVED_REFUNDING("환불 예정됨"),
    REFUNDED("환불됨"),

    REQUESTED_EXCHANGING("교환 요청됨"),
    REJECTED_EXCHANGING("교환 거절됨"),
    EXCHANGED("교환됨");

    private final String message;

    public boolean isUpdatableStatus() {
        return false;
    }

}
