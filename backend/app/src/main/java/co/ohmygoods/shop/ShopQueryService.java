package co.ohmygoods.shop;

import co.ohmygoods.admin.shop.dto.ShopDetailInfo;
import co.ohmygoods.domain.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopQueryService {

    private final ShopRepository shopRepository;

    public ShopDetailInfo getShopDetailInfo(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        return new ShopDetailInfo(
                shop.getName(),
                shop.getIntroduction(),
                shop.getCreatedAt(),
                shop.getShopImagePath().concat(shop.getShopImageName()),
                shop.getStatus());
    }
}
