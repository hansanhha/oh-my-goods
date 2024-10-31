package co.ohmygoods.sale.shop;

import co.ohmygoods.auth.account.AccountRepository;
import co.ohmygoods.auth.account.entity.OAuth2Account;
import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.sale.shop.dto.ShopCreationRequest;
import co.ohmygoods.sale.shop.entity.Shop;
import co.ohmygoods.sale.shop.entity.ShopOwnerChange;
import co.ohmygoods.sale.shop.exception.*;
import co.ohmygoods.sale.shop.vo.ShopOwnerStatus;
import co.ohmygoods.sale.shop.vo.ShopStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final ShopOwnerChangeRepository shopOwnerChangeRepository;

    public Long create(ShopCreationRequest request) {
        var shopName = request.shopName();

        if (shopName == null || shopName.isBlank()) {
            throw InvalidShopNameException.empty();
        }

        var duplicatedName = shopRepository.findByName(shopName);

        duplicatedName.ifPresent(shop -> {
            throw InvalidShopNameException.duplicate(shopName);
        });

        var ownerId = request.ownerId();
        var owner = accountRepository.findByEmail(ownerId)
                .orElseThrow(() -> new AccountNotFoundException(ownerId));

        var shop = Shop.toEntity(shopName, owner, request.shopIntroduction(), ShopStatus.INACTIVE);
        var save = shopRepository.save(shop);
        return save.getId();
    }

    public void inactive(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundShopException("not found shop"));

        shop.changeShopStatus(ShopStatus.INACTIVE);
    }

    public void delete() {

    }

    public void requestChangeOwner(String requestAccountEmail, String targetAccountEmail, Long shopId) {
        var requestAccount = accountRepository.findByEmail(requestAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestAccountEmail));
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        shop.ownerCheck(requestAccount);

        var requestedShopOwnerChange = ShopOwnerChange.toEntity(shop, requestAccount, targetAccount, ShopOwnerStatus.OWNER_CHANGE_REQUESTED);
        shopOwnerChangeRepository.save(requestedShopOwnerChange);
    }

    public void cancelChangeOwnerRequest(String requestedAccountEmail, Long requestedOwnerChangeHistoryId) {
        var requestedAccount = accountRepository.findByEmail(requestedAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestedAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeRepository.findById(requestedOwnerChangeHistoryId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(requestedOwnerChangeHistoryId.toString()));

        var shop = requestedOwnerChangeHistory.getShop();
        shop.ownerCheck(requestedAccount);

        if (requestedAccount.getId().equals(requestedOwnerChangeHistory.getOriginalOwner().getId())) {
            throw UnchangeableShopOwnerException.isNotOwner(requestedAccountEmail, shop.getName());
        }

        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_CANCELED);
    }

    public void approveChangeOwner(String targetAccountEmail, Long requestedOwnerChangeHistoryId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeRepository.findById(requestedOwnerChangeHistoryId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(requestedOwnerChangeHistoryId.toString()));
        var shop = requestedOwnerChangeHistory.getShop();

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_APPROVED);
        shop.changeOwner(targetAccount);
    }

    public void rejectChangeOwner(String targetAccountEmail, Long requestedOwnerChangeHistoryId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeRepository.findById(requestedOwnerChangeHistoryId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(requestedOwnerChangeHistoryId.toString()));

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_REJECTED);
    }

}
