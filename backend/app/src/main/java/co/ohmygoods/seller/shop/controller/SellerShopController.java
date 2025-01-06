package co.ohmygoods.seller.shop.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.seller.shop.service.SellerShopService;
import co.ohmygoods.seller.shop.service.dto.CreateShopRequest;
import co.ohmygoods.seller.shop.controller.dto.CreateShopWebRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@RequestMapping("/api/seller/shop")
@RestController
@RequiredArgsConstructor
public class SellerShopController {

    private final SellerShopService sellerShopService;

    @PostMapping
    @Idempotent
    public Long createShop(@AuthenticationPrincipal AuthenticatedAccount account,
                           @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                           @RequestBody @Validated CreateShopWebRequest request) {

        return sellerShopService.createShop(new CreateShopRequest(account.memberId(),
                request.createShopName(), request.createShopIntroduction()));
    }

    @PatchMapping("/inactive")
    @Idempotent
    public void inactiveShop(@AuthenticationPrincipal AuthenticatedAccount account,
                             @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey) {
        sellerShopService.inactiveShop(account.memberId());
    }

    @DeleteMapping
    public void deleteShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        sellerShopService.deleteShop(account.memberId());
    }
}
