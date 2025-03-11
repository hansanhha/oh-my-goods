package co.ohmygoods.order.service.dto;

import co.ohmygoods.order.model.vo.DeliveryRequirement;
import jakarta.annotation.Nullable;

// 도로명주소(roadNameAddress) 또는 지번주소(lotNumberAddress) 필요
public record RegisterDeliveryAddressRequest(String accountEmail,
                                             String deliveryRecipientName,
                                             String deliveryRecipientPhoneNumber,
                                             String deliveryAddressZipCode,
                                             @Nullable String roadNameAddress,
                                             @Nullable String lotNumberAddress,
                                             String detailAddressInfo,
                                             DeliveryRequirement deliveryRequirement,
                                             boolean isDefaultDeliveryAddress) {
}
