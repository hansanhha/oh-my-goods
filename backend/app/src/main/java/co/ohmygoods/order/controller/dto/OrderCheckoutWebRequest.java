package co.ohmygoods.order.controller.dto;

import java.util.List;

public record OrderCheckoutWebRequest(String orderMemberId,
                                      List<OrderProductDetail> orderDetails,
                                      String orderPaymentMethod,
                                      Long deliveryAddressId,
                                      int totalOrderPrice) {

    public record OrderProductDetail(Long productId,
                                     int PurchaseQuantity,
                                     boolean isAppliedCoupon,
                                     Long appliedCouponId) {
    }
}
