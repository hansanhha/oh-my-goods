package co.ohmygoods.seller.shop.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.seller.shop.service.SellerShopService;
import co.ohmygoods.seller.shop.service.dto.CreateShopRequest;
import co.ohmygoods.seller.shop.controller.dto.CreateShopWebRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/seller/shop")
@RestController
@RequiredArgsConstructor
public class SellerShopController {

    private final SellerShopService sellerShopService;

    @PostMapping
    public Long createShop(@AuthenticationPrincipal AuthenticatedAccount account,
                                     @RequestBody CreateShopWebRequest request) {

        return sellerShopService.createShop(new CreateShopRequest(account.memberId(),
                request.createShopName(), request.createShopIntroduction()));
    }

    @PatchMapping("/status")
    public void inactiveShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        sellerShopService.inactiveShop(account.memberId());
    }

    @DeleteMapping
    public void deleteShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        sellerShopService.deleteShop(account.memberId());
    }
}
