package co.ohmygoods.order.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CancelOrderStatus {

    CANCELED_ORDER("주문 취소됨"),
    REQUESTED_CANCEL_ORDER("주문 취소 요청됨"),
    REJECTED_CANCEL_ORDER("주문 취소 거절됨");

    private final String message;
}
