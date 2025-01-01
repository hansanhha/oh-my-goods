package co.ohmygoods.shop.web.controller;

import co.ohmygoods.shop.service.ShopService;
import co.ohmygoods.shop.service.dto.ShopOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/shop")
@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/{shopId}")
    public ShopOverviewResponse getShop(@PathVariable Long shopId) {
        return shopService.getShopOverview(shopId);
    }
}
