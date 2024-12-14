package co.ohmygoods.order.service;

import co.ohmygoods.order.service.dto.OrderReadyRequest;
import co.ohmygoods.order.service.dto.OrderReadyResponse;

import java.util.UUID;

/*
todo
    - 재고 차감 시점(주문 시, 주문 완료 시)
    - 재고 차감 방법(재고 임시 예약 후 일정 시간 내 결제 완료가 되지 않으면 예약 취소)
    - 주문 유형에 따른 재고 차감 방식 변화(한정판 상품: 주문 요청 시 차감, 일반 상품: 주문 완료 시 차감)
*/
public interface OrderReadyService {

    OrderReadyResponse readyOrder(OrderReadyRequest request);

    default String generateOrderNumber() {
        return UUID.randomUUID().toString();
    }
}
