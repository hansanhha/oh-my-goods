package co.ohmygoods.seller.shop.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.exception.AccountNotFoundException;
import co.ohmygoods.shop.exception.InvalidShopNameException;
import co.ohmygoods.shop.exception.InvalidShopOwnerException;
import co.ohmygoods.shop.exception.NotFoundShopException;
import co.ohmygoods.seller.shop.service.dto.CreateShopRequest;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.model.vo.ShopStatus;
import co.ohmygoods.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
            throw InvalidShopNameException.empty();
        }

        Account account = accountRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AccountNotFoundException(memberId));

        if (shopRepository.existsByOwner(account)) {
            throw new InvalidShopOwnerException("");
        }

        if (shopRepository.existsByName(shopName)) {
            throw InvalidShopNameException.duplicate(shopName);
        }

        Shop shop = Shop.toEntity(shopName, account, request.shopIntroduction(), ShopStatus.INACTIVE);
        Shop save = shopRepository.save(shop);
        return save.getId();
    }

    public void inactiveShop(String ownerMemberId) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(() -> new NotFoundShopException(""));

        shop.changeShopStatus(ShopStatus.INACTIVE);
    }

    public void deleteShop(String ownerMemberId) {
        Shop shop = shopRepository.findByOwnerMemberId(ownerMemberId).orElseThrow(() -> new NotFoundShopException(""));

        shop.changeShopStatus(ShopStatus.DELETE_SCHEDULED);
    }

}
