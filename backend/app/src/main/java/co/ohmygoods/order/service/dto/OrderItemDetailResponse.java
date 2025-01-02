package co.ohmygoods.order.service.dto;

import co.ohmygoods.order.model.entity.OrderItem;

import java.time.LocalDateTime;

public record OrderItemDetailResponse(Long orderItemId,
                                      String orderStatus,
                                      String orderNumber,
                                      String productName,
                                      DeliveryAddressResponse deliveryAddress,
                                      int productOriginalPrice,
                                      int productDiscountPrice,
                                      int couponDiscountPrice,
                                      int finalOrderPrice,
                                      int purchaseQuantity,
                                      int purchasePrice,
                                      LocalDateTime purchaseAt) {

    public static OrderItemDetailResponse from(OrderItem orderItem) {
        return new OrderItemDetailResponse(
                orderItem.getId(),
                orderItem.getOrderStatus().name(),
                orderItem.getOrderNumber(),
                orderItem.getProduct().getName(),
                DeliveryAddressResponse.from(orderItem.getDeliveryAddress()),
                orderItem.getOriginalPrice(),
                orderItem.getProductDiscountPrice(),
                orderItem.getCouponDiscountPrice(),
                orderItem.getPurchasePrice(),
                orderItem.getOrderQuantity(),
                orderItem.getPurchasePrice(),
                orderItem.getCreatedAt()
        );
    }
}
