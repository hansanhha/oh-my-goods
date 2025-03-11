package co.ohmygoods.seller.shop.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.seller.shop.service.SellerShopService;
import co.ohmygoods.seller.shop.service.dto.CreateShopRequest;
import co.ohmygoods.seller.shop.controller.dto.CreateShopWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "판매자 상점", description = "판매자 상점 관련 api")
@RequestMapping("/api/seller/shop")
@RestController
@RequiredArgsConstructor
public class SellerShopController {

    private final SellerShopService sellerShopService;

    @Operation(summary = "판매자 상점 생성", description = "판매자가 상점을 생성합니다 " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상점 생성 완료")
    )
    @PostMapping
    @Idempotent
    public Long createShop(@AuthenticationPrincipal AuthenticatedAccount account,
                           @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                           @RequestBody @Validated CreateShopWebRequest request) {

        return sellerShopService.createShop(new CreateShopRequest(account.memberId(),
                request.createShopName(), request.createShopIntroduction()));
    }

    @Operation(summary = "판매자 상점 비활성화", description = "판매자의 상점을 비활성화합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 비활성화 완료")
    })
    @PatchMapping("/inactive")
    public void inactiveShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        sellerShopService.inactiveShop(account.memberId());
    }

    @Operation(summary = "판매자 상점 삭제", description = "판매자의 상점을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 삭제 완료")
    })
    @DeleteMapping
    public void deleteShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        sellerShopService.deleteShop(account.memberId());
    }
}
