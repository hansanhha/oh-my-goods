package co.ohmygoods.order.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RefundStatus {

    REQUESTED_REFUNDING("환불 요청됨"),
    REJECTED_REFUNDING("환불 거절됨"),
    APPROVED_REFUNDING("환불 예정됨"),
    REFUNDED("환불됨");

    private final String message;
}
