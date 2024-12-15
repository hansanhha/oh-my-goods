package co.ohmygoods.order.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    ORDER_FAILED_OUT_OF_STOCK("주문 수량 초과"),
    ORDER_FAILED_INVALID_ADDRESS("유효하지 않는 배송지"),
    ORDER_FAILED_UNKNOWN("기타"),
    ORDER_FAILED_LACK_QUANTITY("주문 실패(수량 부족)"),
    ORDER_FAILED_INVALID_PRODUCT_STOCK_STATUS("주문 실패(판매 상품 아님)"),
    ORDER_FAILED_PAYMENT_CANCEL("주문 실패(결제 취소)"),
    ORDER_FAILED_PAYMENT_FAILURE("주문 실패(결제 실패)"),

    ORDER_START("주문 시작"),
    ORDERED("주문 완료"),

    ORDER_ITEM_PACKAGING("배송 대기 중"),
    ORDER_ITEM_DELIVERING("배송 중"),
    ORDER_ITEM_DELIVERED("배송 완료"),
    ORDER_ITEM_COMPLETED("구매 확정"),

    ORDER_ITEM_CANCELED_ORDER("주문 취소됨"),
    ORDER_ITEM_REQUESTED_CANCEL_ORDER("주문 취소 요청됨"),
    ORDER_ITEM_REJECTED_CANCEL_ORDER("주문 취소 거절됨"),

    ORDER_ITEM_REQUESTED_REFUNDING("환불 요청됨"),
    ORDER_ITEM_REJECTED_REFUNDING("환불 거절됨"),
    ORDER_ITEM_APPROVED_REFUNDING("환불 예정됨"),
    ORDER_ITEM_REFUNDED("환불됨"),

    ORDER_ITEM_REQUESTED_EXCHANGING("교환 요청됨"),
    ORDER_ITEM_REJECTED_EXCHANGING("교환 거절됨"),
    ORDER_ITEM_EXCHANGED("교환됨");

    private final String message;

    public boolean isNotUpdatableOrderStatus() {
        return this.equals(OrderStatus.ORDERED) || this.equals(OrderStatus.ORDER_ITEM_PACKAGING);
    }

}
