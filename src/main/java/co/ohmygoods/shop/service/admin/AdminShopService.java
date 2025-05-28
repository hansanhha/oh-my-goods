package co.ohmygoods.shop.service.admin;


import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.exception.AuthException;
import co.ohmygoods.shop.exception.ShopException;
import co.ohmygoods.shop.service.admin.dto.CreateShopRequest;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.model.vo.ShopStatus;
import co.ohmygoods.shop.repository.ShopRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AdminShopService {

    private final AccountRepository accountRepository;
    private final ShopRepository shopRepository;

    public Long createShop(CreateShopRequest request) {
        String shopName = request.shopName();
        String memberId = request.memberId();

        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);

        if (shopRepository.existsByAdmin(account)) {
            throw ShopException.ALREADY_EXIST_SHOP_OWNER;
        }

        if (shopRepository.existsByName(shopName)) {
            throw ShopException.ALREADY_EXIST_SHOP;
        }

        Shop shop = Shop.toEntity(shopName, account, request.shopIntroduction(), ShopStatus.ACTIVE);
        Shop save = shopRepository.save(shop);
        return save.getId();
    }

    public void deleteShop(String ownerMemberId) {
        Shop shop = shopRepository.findByAdminMemberId(ownerMemberId).orElseThrow(ShopException::notFoundShop);

        shop.changeShopStatus(ShopStatus.DELETED);
    }

}
