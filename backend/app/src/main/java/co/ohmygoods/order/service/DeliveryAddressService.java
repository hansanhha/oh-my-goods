package co.ohmygoods.order.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.order.exception.DeliveryAddressException;
import co.ohmygoods.order.model.entity.DeliveryAddress;
import co.ohmygoods.order.repository.DeliveryAddressRepository;
import co.ohmygoods.order.service.dto.RegisterDeliveryAddressRequest;
import co.ohmygoods.order.service.dto.DeliveryAddressResponse;
import co.ohmygoods.order.service.dto.UpdateDeliveryAddressRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final AccountRepository accountRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public List<DeliveryAddressResponse> getDeliveryAddressesByAccount(String accountEmail) {
        List<DeliveryAddress> deliveryAddresses = deliveryAddressRepository.findAllByAccountEmail(accountEmail);

        return deliveryAddresses.stream().map(DeliveryAddressResponse::from).toList();
    }

    public DeliveryAddressResponse registerAddress(RegisterDeliveryAddressRequest request) {
        if (!StringUtils.hasText(request.roadNameAddress()) && !StringUtils.hasText(request.lotNumberAddress())) {
            throw new DeliveryAddressException();
        }

        Account account = accountRepository.findByEmail(request.accountEmail()).orElseThrow(DeliveryAddressException::new);

        DeliveryAddress deliveryAddress = DeliveryAddress.builder()
                .account(account)
                .recipientName(request.deliveryRecipientName())
                .recipientPhoneNumber(request.deliveryRecipientPhoneNumber())
                .zipCode(request.deliveryAddressZipCode())
                .roadNameAddress(request.roadNameAddress())
                .lotNumberAddress(request.lotNumberAddress())
                .detailAddress(request.detailAddressInfo())
                .deliveryRequirement(request.deliveryRequirement())
                .build();

        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);

        return DeliveryAddressResponse.from(saved);
    }

    public DeliveryAddressResponse updateAddress(UpdateDeliveryAddressRequest request) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findById(request.updateDeliveryAddressId()).orElseThrow(DeliveryAddressException::new);

        deliveryAddress.update(request.deliveryRecipientName(), request.deliveryRecipientPhoneNumber(),
                request.deliveryAddressZipCode(), request.roadNameAddress(), request.lotNumberAddress(),
                request.detailAddressInfo(), request.deliveryRequirement(), request.isDefaultDeliveryAddress());

        return DeliveryAddressResponse.from(deliveryAddress);
    }

}
