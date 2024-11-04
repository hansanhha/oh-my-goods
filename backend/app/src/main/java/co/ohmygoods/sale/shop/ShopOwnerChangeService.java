package co.ohmygoods.sale.shop;

import co.ohmygoods.auth.account.AccountRepository;
import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.sale.shop.dto.ShopOwnerChangeHistory;
import co.ohmygoods.sale.shop.exception.ShopNotFoundException;
import co.ohmygoods.sale.shop.exception.ShopOwnerChangeHistoryException;
import co.ohmygoods.sale.shop.exception.ShopOwnerChangeNotFoundException;
import co.ohmygoods.sale.shop.exception.UnchangeableShopOwnerException;
import co.ohmygoods.sale.shop.vo.ShopOwnerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopOwnerChangeService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;
    private final ShopOwnerChangeHistoryRepository shopOwnerChangeHistoryRepository;

    public ShopOwnerChangeHistory getRequestHistory(Long historyId) {
        var shopOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeHistoryException(historyId.toString()));

        return new ShopOwnerChangeHistory(historyId,
                shopOwnerChangeHistory.getOriginalOwner().getEmail(),
                shopOwnerChangeHistory.getTargetAccount().getEmail(),
                shopOwnerChangeHistory.getStatus(),
                shopOwnerChangeHistory.getCreatedAt());
    }

    public void requestChange(String requestAccountEmail, String targetAccountEmail, Long shopId) {
        var requestAccount = accountRepository.findByEmail(requestAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestAccountEmail));
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        shop.ownerCheck(requestAccount);

        var requestedShopOwnerChange = co.ohmygoods.sale.shop.entity.ShopOwnerChangeHistory.toEntity(shop, requestAccount, targetAccount, ShopOwnerStatus.OWNER_CHANGE_REQUESTED);
        shopOwnerChangeHistoryRepository.save(requestedShopOwnerChange);
    }

    public void cancelRequest(String requestAccountEmail, Long historyId) {
        var requestedAccount = accountRepository.findByEmail(requestAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));

        var shop = requestedOwnerChangeHistory.getShop();
        shop.ownerCheck(requestedAccount);

        if (requestedAccount.getId().equals(requestedOwnerChangeHistory.getOriginalOwner().getId())) {
            throw UnchangeableShopOwnerException.isNotOwner(requestAccountEmail, shop.getName());
        }

        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_CANCELED);
    }

    public void approveChangeRequest(String targetAccountEmail, Long historyId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));
        var shop = requestedOwnerChangeHistory.getShop();

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_APPROVED);
        shop.changeOwner(targetAccount);
    }

    public void rejectChangeRequest(String targetAccountEmail, Long historyId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_REJECTED);
    }
}
