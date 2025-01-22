package co.ohmygoods.order.controller.dto;

import co.ohmygoods.payment.model.vo.ExternalPaymentVendor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record OrderCheckoutWebRequest(
        @Schema(description = "최소 한 개 이상의 상품부터 주문할 수 있습니다")
        @NotNull(message = "주문할 상품이 없습니다") @Size(min = 1, message = "주문할 상품이 없습니다")
        List<OrderProductDetail> orderDetails,

        @Schema(description = "간편 결제 제공자를 입력해주세요", examples = {"KAKAO", "kakao"})
        @NotEmpty(message = "결제 수단을 입력해주세요")
        AllowedOrderCheckoutPaymentMethod orderPaymentMethod,

        @Schema(description = "사용자가 등록한 배송지 아이디 중 하나를 입력합니다, 배송지가 하나라도 없는 경우 주문을 진행하기 전 만들어야 합니다")
        @NotNull(message = "올바르지 않은 배송지 id입니다") @Positive(message = "올바르지 않은 배송지 id입니다")
        Long deliveryAddressId,

        @Schema(description = "할인을 적용하지 않은 모든 상품의 가격")
        @NotNull(message = "올바르지 않은 주문 가격입니다") @Positive(message = "올바르지 않은 주문 가격입니다")
        int totalOrderPrice) {

    public record OrderProductDetail(
            @Schema(description = "주문할 상품 아이디")
            @NotNull(message = "올바르지 않은 상품 id입니다") @Positive(message = "올바르지 않은 상품 id입니다")
            Long productId,

            @Schema(description = "주문할 상품 개수")
            @NotNull(message = "올바르지 않은 주문 상품 개수입니다") @Positive(message = "올바르지 않은 주문 상품 개수입니다")
            int PurchaseQuantity,

            @Schema(description = "쿠폰 적용 여부")
            Boolean isAppliedCoupon,

            @Schema(description = "적용할 쿠폰 아이디")
            Long appliedCouponId) {
    }

    public enum AllowedOrderCheckoutPaymentMethod {
        kakao,
        KAKAO,
    }
}
