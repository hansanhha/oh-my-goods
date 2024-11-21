package co.ohmygoods.order.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.AddressException;
import co.ohmygoods.order.vo.DeliveryRequirement;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private OAuth2Account account;

    private String recipientName;

    private String zipCode;

    @Column(nullable = false)
    private String address;

    private String phone;

    @Column(nullable = false)
    private String detailAddress;

    @Convert(converter = DeliveryRequirement.DatabaseConverter.class)
    @Column(nullable = false)
    private DeliveryRequirement deliveryRequirement;

    private boolean defaultAddress;

    public void updateRecipient(String recipientName, String phone) {
        if (!StringUtils.hasText(recipientName) || !StringUtils.hasText(phone)) {
            AddressException.throwCauseInvalidInput(recipientName, phone);
        }

        this.recipientName = recipientName;
        this.phone = phone;
    }

    public void updateAddress(String address, String detailAddress, String zipCode) {
        if (!StringUtils.hasText(address) || !StringUtils.hasText(detailAddress) || !StringUtils.hasText(zipCode)) {
            AddressException.throwCauseInvalidInput(address, detailAddress, zipCode);
        }

        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    public void updateDeliveryRequirement(DeliveryRequirement deliveryRequirement) {
        this.deliveryRequirement = deliveryRequirement;
    }

    public void setDefaultAddress() {
        defaultAddress = true;
    }
}
