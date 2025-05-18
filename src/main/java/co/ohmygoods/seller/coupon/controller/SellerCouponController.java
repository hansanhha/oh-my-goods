package co.ohmygoods.seller.coupon.controller;

import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.seller.coupon.controller.dto.CreateShopCouponWebRequest;
import co.ohmygoods.seller.coupon.service.SellerCouponService;
import co.ohmygoods.seller.coupon.service.dto.CreateShopCouponRequest;
import co.ohmygoods.seller.coupon.service.dto.ShopCouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "판매자 쿠폰", description = "판매자 쿠폰 관련 api")
@RequestMapping("/api/seller/coupon")
@RestController
@RequiredArgsConstructor
public class SellerCouponController {

    private final SellerCouponService couponService;

    @Operation(summary = "판매자의 상점 쿠폰 발행 내역 조회", description = "판매자가 발행한 상점 쿠폰 발행 내역을 최신순으로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 쿠폰 발행 내역 반환")
    })
    @GetMapping("/history")
    public ResponseEntity<?> getShopCouponHistory(@AuthenticationPrincipal AuthenticatedAccount account,
                                                  @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                                  @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ShopCouponResponse> couponHistory = couponService.getShopCouponCreationHistory(account.memberId(), Pageable.ofSize(size).withPage(page));
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
                                              @RequestBody @Validated CreateShopCouponWebRequest request) {

        CreateShopCouponRequest createShopCouponRequest = CreateShopCouponRequest.builder()
                .sellerMemberId(account.memberId())
                .isLimitedMaxIssueCount(request.isLimitedMaxIssueCount())
                .maxIssueCount(request.maxIssueCount())
                .isLimitedUsageCountPerAccount(request.isLimitedUsageCountPerAccount())
                .usageCountPerAccount(request.usageCountPerAccount())
                .isFixedDiscount(request.isFixedDiscount())
                .discountValue(request.discountValue())
                .minimumPurchasePrice(request.minimumPurchasePrice())
                .isApplicableSpecificProducts(request.isApplicableSpecificProducts())
                .applicableProductIds(request.applicableProductIds())
                .couponName(request.couponName())
                .couponCode(request.couponCode())
                .maxDiscountPrice(request.maxDiscountPrice())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        ShopCouponResponse created = couponService.createShopCoupon(createShopCouponRequest);
        return ResponseEntity.created(URI.create("")).body(Map.of("data", created));
    }

    @Operation(summary = "판매자의 상점 쿠폰 삭제", description = "판매자가 상점 쿠폰을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 쿠폰 삭제 완료")
    })
    @DeleteMapping("/{couponId}")
    public void deleteShopCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                                 @PathVariable Long couponId) {
        couponService.destroyIssuingShopCoupon(account.memberId(), couponId);
    }
}
