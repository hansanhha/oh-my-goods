package co.ohmygoods.sale.shop;

import co.ohmygoods.sale.shop.dto.ShopDto;
import co.ohmygoods.sale.shop.exception.ShopNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ShopUserService {

    private final ShopRepository shopRepository;

    public ShopDto getOne(Long shopId) {
        var shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopNotFoundException(shopId.toString()));

        return new ShopDto(shop.getName(), shop.getIntroduction(), shop.getCreatedAt(), shop.getStatus());
    }
}
