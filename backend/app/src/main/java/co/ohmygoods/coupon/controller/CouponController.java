package co.ohmygoods.coupon.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.coupon.service.CouponService;
import co.ohmygoods.coupon.service.dto.ApplicableIssuedCouponResponse;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "쿠폰", description = "쿠폰 관련 api")
@RequestMapping("/api/coupons")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "보유한 쿠폰 목록 조회", description = "사용자가 발급받은 모든 쿠폰 목록 또는 특정 상품에 적용할 수 있는 쿠폰 목록을 조회합니다, " +
            "쿼리 스트링에 특정 상품을 지정하면 해당 상품에 적용할 수 있는 쿠폰 목록만 반환합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자가 발급한 모든 쿠폰 목록 반환"),
            @ApiResponse(responseCode = "200", description = "사용자가 발급한 쿠폰 목록 중 해당 상품에 적용할 수 있는 쿠폰 목록 반환")
    })
    @GetMapping
    public ResponseEntity<?> getCoupons(@AuthenticationPrincipal AuthenticatedAccount account,
                                        @Parameter(name = "상품 아이디", description = "해당 상품에 적용할 수 있는 쿠폰 목록을 조회합니다")
                                        @RequestParam(required = false, defaultValue = "-1") Long productId,
                                        @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                        @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        Slice<ApplicableIssuedCouponResponse> coupons;

        if (productId > 0) {
             coupons = couponService.getAllApplicableIssuedCoupons(account.memberId(), Pageable.ofSize(size).withPage(page));
        } else {
            coupons = couponService.getIssuedCouponsApplicableToProduct(account.memberId(), productId, Pageable.ofSize(size).withPage(page));
        }

        return ResponseEntity.ok(coupons);
    }

    @Operation(summary = "쿠폰 발급", description = IdempotencyOpenAPI.message)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "쿠폰 발급 성공"),
    })
    @PostMapping("/{couponId}/issue")
    @Idempotent
    public void issueCoupon(@AuthenticationPrincipal AuthenticatedAccount account,
                            @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                            @Parameter(name ="쿠폰 아이디", in = ParameterIn.PATH) @PathVariable("couponId")
                            @Positive(message = "올바르지 않은 쿠폰 id입니다") Long couponId) {
        couponService.issueCoupon(account.memberId(), couponId);
    }
}
