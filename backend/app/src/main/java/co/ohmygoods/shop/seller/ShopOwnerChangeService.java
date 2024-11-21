package co.ohmygoods.shop.seller;

import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.shop.seller.dto.ShopOwnerChangeHistoryDto;
import co.ohmygoods.shop.exception.ShopNotFoundException;
import co.ohmygoods.shop.exception.ShopOwnerChangeHistoryException;
import co.ohmygoods.shop.exception.ShopOwnerChangeNotFoundException;
import co.ohmygoods.shop.seller.entity.ShopOwnerChangeHistory;
import co.ohmygoods.shop.seller.vo.ShopOwnerStatus;
import co.ohmygoods.shop.repository.ShopRepository;
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

    public ShopOwnerChangeHistoryDto getHistory(Long historyId) {
        var shopOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeHistoryException(historyId.toString()));

        return new ShopOwnerChangeHistoryDto(historyId,
                shopOwnerChangeHistory.getOriginalOwner().getEmail(),
                shopOwnerChangeHistory.getTargetAccount().getEmail(),
                shopOwnerChangeHistory.getStatus(),
                shopOwnerChangeHistory.getCreatedAt());
    }

    public void request(String requestAccountEmail, String targetAccountEmail, Long shopId) {
        var requestAccount = accountRepository.findByEmail(requestAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestAccountEmail));
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        shop.ownerCheck(requestAccount);

        var requestedShopOwnerChange = ShopOwnerChangeHistory.toEntity(shop, requestAccount, targetAccount, ShopOwnerStatus.OWNER_CHANGE_REQUESTED);
        shopOwnerChangeHistoryRepository.save(requestedShopOwnerChange);
    }

    public void cancel(String requestAccountEmail, Long historyId) {
        var requestedAccount = accountRepository.findByEmail(requestAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(requestAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));

        var shop = requestedOwnerChangeHistory.getShop();
        shop.ownerCheck(requestedAccount);

        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_CANCELED);
    }

    public void approve(String targetAccountEmail, Long historyId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));
        var shop = requestedOwnerChangeHistory.getShop();

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_APPROVED);
        shop.changeOwner(targetAccount);
    }

    public void reject(String targetAccountEmail, Long historyId) {
        var targetAccount = accountRepository.findByEmail(targetAccountEmail)
                .orElseThrow(() -> new AccountNotFoundException(targetAccountEmail));
        var requestedOwnerChangeHistory = shopOwnerChangeHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ShopOwnerChangeNotFoundException(historyId.toString()));

        requestedOwnerChangeHistory.targetAccountCheck(targetAccount);
        requestedOwnerChangeHistory.changeShopOwnerStatus(ShopOwnerStatus.OWNER_CHANGE_REJECTED);
    }
}
