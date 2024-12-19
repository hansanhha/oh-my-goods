package co.ohmygoods.seller.shop.model.entity;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.global.entity.BaseEntity;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.exception.UnchangeableShopOwnerException;
import co.ohmygoods.seller.shop.model.vo.ShopOwnerStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ShopOwnerChangeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_owner_id")
    private Account originalOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id")
    private Account targetAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopOwnerStatus status;

    public static ShopOwnerChangeHistory toEntity(Shop shop, Account originalOwner, Account targetAccount, ShopOwnerStatus status) {
        var shopOwnerChange = new ShopOwnerChangeHistory();
        shopOwnerChange.shop = shop;
        shopOwnerChange.originalOwner = originalOwner;
        shopOwnerChange.targetAccount = targetAccount;
        shopOwnerChange.status = status;
        return shopOwnerChange;
    }

    public void targetAccountCheck(Account account) {
        if (!targetAccount.getEmail().equals(account.getEmail())) {
            throw UnchangeableShopOwnerException.isNotTargetAccount(account.getEmail(), shop.getName());
        }
    }

    public void changeShopOwnerStatus(ShopOwnerStatus status) {
        if (!getStatus().isChangeable(status)) {
            throw UnchangeableShopOwnerException.unchangeable(getStatus().name(), status.name());
        }

        this.status = status;
    }
}
