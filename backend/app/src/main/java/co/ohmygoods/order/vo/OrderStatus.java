package co.ohmygoods.order.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PAYING("결제 중"),
    PAYMENT_FAILED("결제 실패"),
    OUT_OF_STOCK("주문 수량 초과"),
    INVALID_ADDRESS("유효하지 않는 배송지"),
    FAILED_OTHER("기타"),

    ORDERED("주문 완료"),
    PACKAGING("배송 대기 중"),
    DELIVERING("배송 중"),
    DELIVERED("배송 완료"),
    COMPLETED("구매 확정"),

    CANCEL_ORDER_BEFORE_PACKAGING("주문 취소됨"),
    REQUESTED_CANCEL_ORDER("주문 취소 요청됨"),
    REJECTED_CANCEL_ORDER("주문 취소 거절됨"),
    CANCEL_ORDER_AFTER_PACKAGING("주문 취소됨"),

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
