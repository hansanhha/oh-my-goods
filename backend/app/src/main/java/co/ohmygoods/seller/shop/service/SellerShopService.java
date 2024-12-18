package co.ohmygoods.seller.shop.service;

import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.shop.exception.InvalidShopNameException;
import co.ohmygoods.shop.exception.NotFoundShopException;
import co.ohmygoods.seller.shop.service.dto.CreateShopRequest;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.model.vo.ShopStatus;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerShopService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;

    public Long createShop(CreateShopRequest request) {
        var shopName = request.shopName();

        if (shopName == null || shopName.isBlank()) {
            throw InvalidShopNameException.empty();
        }

        var duplicatedName = shopRepository.findByName(shopName);

        duplicatedName.ifPresent(shop -> {
            throw InvalidShopNameException.duplicate(shopName);
        });

        var ownerId = request.ownerEmail();
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
