package co.ohmygoods.sale.shop.entity;

import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.sale.shop.vo.ShopStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "onwer_id")
    private OAuth2Account owner;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Enumerated(EnumType.STRING)
    private ShopStatus status;
}
