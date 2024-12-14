package co.ohmygoods.order.service.dto;

import java.util.List;

/*
    필요 정보
    - 주문자 정보
    - 상품 정보 & 상품 쿠폰 정보
    - 장바구니 쿠폰 정보
    - 결제 정보
    - 총 결제 금액
*/
public record OrderReadyRequest(String orderAccountEmail,
                                List<OrderProductDetail> orderDetails,
                                OrderPaymentMethod orderPaymentMethod,
                                Long deliveryAddressId,
                                int totalOrderPrice) {

    public record OrderProductDetail(Long productId,
                                     int purchaseQuantity,
                                     boolean isAppliedCoupon,
                                     Long appliedCouponId) {
    }

    /*
        (임시)
        payment 도메인의 vo 객체를 order 도메인의 service dto에서 사용하기 애매하다고 생각돼서 동일한 값을 가진 dto 사용
     */
    public enum OrderPaymentMethod {
        KAKAOPAY,
        NAVERPAY;
    }

}
