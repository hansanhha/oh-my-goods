package co.ohmygoods.order.service.dto;

import co.ohmygoods.payment.vo.ExternalPaymentVendor;

import java.util.List;

/*
    필요 정보
    - 주문자 정보
    - 상품 정보 & 상품 쿠폰 정보
    - 장바구니 쿠폰 정보
    - 결제 정보
    - 총 결제 금액
*/
public record OrderStartRequest(String orderAccountEmail,
                                List<OrderProductDetail> orderDetails,
                                ExternalPaymentVendor orderPaymentMethod,
                                Long deliveryAddressId,
                                int totalOrderPrice) {

    public record OrderProductDetail(Long productId,
                                     int purchaseQuantity,
                                     boolean isAppliedCoupon,
                                     Long appliedCouponId) {
    }

}
