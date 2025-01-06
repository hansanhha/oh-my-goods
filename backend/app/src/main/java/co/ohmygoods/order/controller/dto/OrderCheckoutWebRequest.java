package co.ohmygoods.order.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCheckoutWebRequest(@NotNull(message = "주문할 상품이 없습니다") @Size(min = 1, message = "주문할 상품이 없습니다")
                                      List<OrderProductDetail> orderDetails,
                                      @NotEmpty(message = "결제 수단을 입력해주세요")
                                      AllowedOrderCheckoutPaymentMethod orderPaymentMethod,
                                      @NotNull(message = "올바르지 않은 배송지 id입니다") @Positive(message = "올바르지 않은 배송지 id입니다")
                                      Long deliveryAddressId,
                                      @NotNull(message = "올바르지 않은 주문 가격입니다") @Positive(message = "올바르지 않은 주문 가격입니다")
                                      int totalOrderPrice) {

    public record OrderProductDetail(@NotNull(message = "올바르지 않은 상품 id입니다") @Positive(message = "올바르지 않은 상품 id입니다")
                                     Long productId,
                                     @NotNull(message = "올바르지 않은 주문 상품 개수입니다") @Positive(message = "올바르지 않은 주문 상품 개수입니다")
                                     int PurchaseQuantity,
                                     Boolean isAppliedCoupon,
                                     Long appliedCouponId) {
    }

    public enum AllowedOrderCheckoutPaymentMethod {
        kakao,
        KAKAO,
    }
}
