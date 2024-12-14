package co.ohmygoods.order.service.dto;

import co.ohmygoods.order.model.vo.DeliveryRequirement;
import jakarta.annotation.Nullable;

public record UpdateDeliveryAddressRequest(Long updateDeliveryAddressId,
                                           String deliveryRecipientName,
                                           String deliveryRecipientPhoneNumber,
                                           String deliveryAddressZipCode,
                                           @Nullable String roadNameAddress,
                                           @Nullable String lotNumberAddress,
                                           String detailAddressInfo,
                                           DeliveryRequirement deliveryRequirement,
                                           boolean isDefaultDeliveryAddress) {
}
