package co.ohmygoods.shop.controller.admin;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.shop.controller.admin.dto.CreateShopWebRequest;
import co.ohmygoods.shop.service.admin.AdminShopService;
import co.ohmygoods.shop.service.admin.dto.CreateShopRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;


@Tag(name = "상점 관리", description = "상점 생성/삭제 관련 api")
@RequestMapping("/api/admin/shop")
@RestController
@RequiredArgsConstructor
public class ShopAdminController {

    private final AdminShopService adminShopService;

    @Operation(summary = "상점 생성", description = "상점을 생성합니다 " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상점 생성 완료")
    )
    @PostMapping
    @Idempotent
    public Long createShop(@AuthenticationPrincipal AuthenticatedAccount account,
                           @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                           @RequestBody @Validated CreateShopWebRequest request) {

        return adminShopService.createShop(new CreateShopRequest(account.memberId(),
                request.createShopName(), request.createShopIntroduction()));
    }

    @Operation(summary = "상점 삭제", description = "상점을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 삭제 완료")
    })
    @DeleteMapping
    public void deleteShop(@AuthenticationPrincipal AuthenticatedAccount account) {
        adminShopService.deleteShop(account.memberId());
    }
}
