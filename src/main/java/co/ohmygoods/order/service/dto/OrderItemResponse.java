package co.ohmygoods.order.service.dto;

import co.ohmygoods.order.model.entity.OrderItem;

import java.time.LocalDateTime;

public record OrderItemResponse(Long orderItemId,
                                String orderStatus,
                                String orderNumber,
                                String productName,
                                String deliveryRoadNameAddress,
                                String deliveryLotNumberAddress,
                                int purchaseQuantity,
                                int purchasePrice,
                                LocalDateTime purchaseAt) {

    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getOrderStatus().name(),
                orderItem.getOrderNumber(),
                orderItem.getProduct().getName(),
                orderItem.getDeliveryAddress().getRoadNameAddress(),
                orderItem.getDeliveryAddress().getLotNumberAddress(),
                orderItem.getOrderQuantity(),
                orderItem.getPurchasePrice(),
                orderItem.getCreatedAt()
        );
    }
}
