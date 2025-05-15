package co.ohmygoods.order.service.dto;

import co.ohmygoods.order.model.entity.DeliveryAddress;
import lombok.Builder;

public record DeliveryAddressResponse(Long deliveryAddressId,
                                      String deliveryRecipientName,
                                      String deliveryRecipientPhoneNumber,
                                      String deliveryAddressZipCode,
                                      String deliveryRoadNameAddress,
                                      String deliveryLotNumberAddress,
                                      String deliveryDetailAddress,
                                      String deliveryRequirement,
                                      boolean isDefaultDeliveryAddress) {

    public static DeliveryAddressResponse from(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressResponse(
                deliveryAddress.getId(),
                deliveryAddress.getRecipientName(),
                deliveryAddress.getRecipientPhoneNumber(),
                deliveryAddress.getZipCode(),
                deliveryAddress.getRoadNameAddress(),
                deliveryAddress.getLotNumberAddress(),
                deliveryAddress.getDetailAddress(),
                deliveryAddress.getDeliveryRequirement().requirement(),
                deliveryAddress.isDefaultDeliveryAddress()
        );
    }
}
