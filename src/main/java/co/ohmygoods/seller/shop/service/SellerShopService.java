package co.ohmygoods.seller.shop.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.seller.shop.exception.SellerShopException;
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
        String shopName = request.shopName();
        String memberId = request.memberId();

        if (shopName == null || shopName.isBlank()) {
            throw SellerShopException.INVALID_SHOP_CREATION_INFO;
        }

        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        if (shopRepository.existsByOwner(account)) {
            throw SellerShopException.ALREADY_EXIST_SHOP_OWNER;
        }

        if (shopRepository.existsByName(shopName)) {
            throw SellerShopException.ALREADY_EXIST_SHOP;
        }

        Shop shop = Shop.toEntity(shopName, account, request.shopIntroduction(), ShopStatus.INACTIVE);
        Shop save = shopRepository.save(shop);
        return save.getId();
    }

    public void inactiveShop(String ownerMemberId) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(SellerShopException::notFoundShop);

        shop.changeShopStatus(ShopStatus.INACTIVE);
    }

    public void deleteShop(String ownerMemberId) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(SellerShopException::notFoundShop);

        shop.changeShopStatus(ShopStatus.DELETE_SCHEDULED);
    }

}
