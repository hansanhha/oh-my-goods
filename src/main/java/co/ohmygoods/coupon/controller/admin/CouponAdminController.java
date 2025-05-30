package co.ohmygoods.coupon.controller.admin;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.coupon.controller.admin.dto.ShopCouponCreateWebRequest;
import co.ohmygoods.coupon.service.admin.CouponAdminService;
import co.ohmygoods.coupon.service.admin.dto.ShopCouponCreateRequest;
import co.ohmygoods.coupon.service.admin.dto.ShopCouponResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;


@Tag(name = "상점 쿠폰 관리", description = "관리자의 상점 쿠폰 관리 api")
@RequestMapping("/api/admin/coupons")
@RestController
@RequiredArgsConstructor
public class CouponAdminController {

    private final CouponAdminService couponService;

    @Operation(summary = "판매자의 상점 쿠폰 발행 내역 조회", description = "판매자가 발행한 상점 쿠폰 발행 내역을 최신순으로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 쿠폰 발행 내역 반환")
    })
    @GetMapping("/history")
    public ResponseEntity<?> getShopCouponHistory(@AuthenticationPrincipal AuthenticatedAccount account,
                                                  @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                                  @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ShopCouponResponse> couponHistory = couponService.getShopCouponCreateHistory(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(couponHistory);
    }

    @Operation(summary = "판매자의 상점 쿠폰 발행", description = "판매자가 상점 쿠폰을 발행합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 쿠폰 발행 완료")
    })
    @PostMapping
    @Idempotent
    public ResponseEntity<?> createShopCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                                              @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                              @RequestBody @Validated ShopCouponCreateWebRequest request) {

        ShopCouponCreateRequest shopCouponCreateRequest = ShopCouponCreateRequest.of(account.memberId(), request);
        ShopCouponResponse created = couponService.createShopCoupon(shopCouponCreateRequest);
        return ResponseEntity.created(URI.create("")).body(Map.of("data", created));
    }

}
