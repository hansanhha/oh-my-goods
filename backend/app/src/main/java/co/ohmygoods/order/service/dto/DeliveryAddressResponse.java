package co.ohmygoods.order.service.dto;

import lombok.Builder;

@Builder
public record DeliveryAddressResponse(Long deliveryAddressId,
                                      String deliveryRecipientName,
                                      String deliveryRecipientPhoneNumber,
                                      String deliveryAddressZipCode,
                                      String deliveryRoadNameAddress,
                                      String deliveryLotNumberAddress,
                                      String deliveryDetailAddress,
                                      String deliveryRequirement,
                                      boolean isDefaultDeliveryAddress) {
}
