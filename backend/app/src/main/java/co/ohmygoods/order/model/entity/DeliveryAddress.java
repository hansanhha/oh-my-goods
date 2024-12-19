package co.ohmygoods.order.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.order.exception.DeliveryAddressException;
import co.ohmygoods.order.model.vo.DeliveryRequirement;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryAddress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private String recipientName;

    private String recipientPhoneNumber;

    private String zipCode;

    private String roadNameAddress;

    private String lotNumberAddress;

    @Column(nullable = false)
    private String detailAddress;

    @Convert(converter = DeliveryRequirement.DatabaseConverter.class)
    private DeliveryRequirement deliveryRequirement;

    private boolean defaultDeliveryAddress;

    public void update(String recipientName,
                       String recipientPhoneNumber,
                       String zipCode,
                       String roadNameAddress,
                       String lotNumberAddress,
                       String detailAddress,
                       DeliveryRequirement deliveryRequirement,
                       boolean defaultDeliveryAddress) {
        if (!StringUtils.hasText(recipientName) || !StringUtils.hasText(recipientPhoneNumber)
                || !StringUtils.hasText(zipCode) || !StringUtils.hasText(detailAddress)
                || (!StringUtils.hasText(roadNameAddress) && !StringUtils.hasText(lotNumberAddress))) {
            throw new DeliveryAddressException();
        }

        this.recipientName = recipientName;
        this.recipientPhoneNumber = recipientPhoneNumber;
        this.zipCode = zipCode;
        this.roadNameAddress = roadNameAddress;
        this.lotNumberAddress = lotNumberAddress;
        this.detailAddress = detailAddress;
        this.deliveryRequirement = deliveryRequirement;
        this.defaultDeliveryAddress = defaultDeliveryAddress;
    }
}
