package co.ohmygoods.shop.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.model.vo.ShopStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
public class Shop extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "admin_id")
    private Account admin;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus status;

    private String shopImagePath;

    private String shopImageName;

    public static Shop toEntity(String name, Account owner, String introduction, ShopStatus status) {
        var shop = new Shop();
        shop.name = name;
        shop.admin = owner;
        shop.introduction = introduction;
        shop.status = status;
        return shop;
    }

    public void changeShopStatus(ShopStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        Shop shop = (Shop) o;
        return Objects.equals(id, shop.id) && Objects.equals(name, shop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
