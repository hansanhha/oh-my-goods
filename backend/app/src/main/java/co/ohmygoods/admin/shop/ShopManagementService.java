package co.ohmygoods.admin.shop;

import co.ohmygoods.auth.account.AccountRepository;
import co.ohmygoods.domain.account.exception.AccountNotFoundException;
import co.ohmygoods.domain.shop.exception.InvalidShopNameException;
import co.ohmygoods.domain.shop.exception.NotFoundShopException;
import co.ohmygoods.admin.shop.dto.ShopCreationRequest;
import co.ohmygoods.domain.shop.entity.Shop;
import co.ohmygoods.domain.shop.vo.ShopStatus;
import co.ohmygoods.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopManagementService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;

    public Long createShop(ShopCreationRequest request) {
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

    public void inactiveShop(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new NotFoundShopException("not found shop"));

        shop.changeShopStatus(ShopStatus.INACTIVE);
    }

    public void deleteShop() {

    }

}
