package co.ohmygoods.product.controller.admin;


import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.product.service.admin.ProductRegistrationAdminService;
import co.ohmygoods.product.service.admin.dto.ProductRegisterRequest;
import co.ohmygoods.product.service.admin.dto.ShopProductResponse;
import co.ohmygoods.product.service.admin.dto.ProductMetadataUpdateRequest;
import co.ohmygoods.product.controller.admin.dto.ProductRegisterWebRequest;
import co.ohmygoods.product.controller.admin.dto.ProductMetadataUpdateWebRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;


@Tag(name = "상점 상품 관리", description = "관리자의 상점 상품 관리 api")
@RequestMapping("/api/admin/product")
@RestController
@RequiredArgsConstructor
public class ProductAdminController {

    private final ProductRegistrationAdminService registrationService;

    @Operation(summary = "판매자 상품 목록 조회", description = "판매자가 등록한 상품 목록을 최신순으로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 반환")
    })
    @GetMapping
    public ResponseEntity<?> getProducts(@AuthenticationPrincipal AuthenticatedAccount account,
                                         @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                         @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        List<ShopProductResponse> registeredProducts = registrationService.getRegisteredProducts(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(registeredProducts);
    }

    @Operation(summary = "판매자 상품 등록", description = "판매자가 자신의 상점에 상품을 등록합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상품 등록 완료")
    )
    @PostMapping
    @Idempotent
    public ResponseEntity<?> registerProduct(@AuthenticationPrincipal AuthenticatedAccount account,
                                             @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                             @RequestBody @Validated ProductRegisterWebRequest request) {

        ProductRegisterRequest productRegisterRequest = ProductRegisterRequest.of(account.memberId(), request);

        ShopProductResponse registered = registrationService.registerProduct(productRegisterRequest);
        return ResponseEntity.created(URI.create("")).body(Map.of("data", registered));
    }

    @Operation(summary = "판매자 상품 수정", description = "판매자가 등록한 상품을 수정합니다. " + IdempotencyOpenAPI.message)
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "상품 수정 완료")
    )
    @PatchMapping("/{productId}/metadata")
    @Idempotent
    public ResponseEntity<?> updateProductMetadata(@AuthenticationPrincipal AuthenticatedAccount account,
                                                   @IdempotencyOpenAPI.HeaderDescription @RequestHeader(IDEMPOTENCY_HEADER) String idempotencyKey,
                                                   @Parameter(name = "수정할 상품 아이디", in = ParameterIn.PATH) @PathVariable Long productId,
                                                   @RequestBody @Validated ProductMetadataUpdateWebRequest request) {

        ProductMetadataUpdateRequest productMetadataUpdateRequest = ProductMetadataUpdateRequest.of(account.memberId(), productId, request);
        ShopProductResponse updated = registrationService.updateProductMetadata(productMetadataUpdateRequest);
        return ResponseEntity.ok(Map.of("data", updated));
    }

    @Operation(summary = "판매자 상품 목록 삭제", description = "판매자가 등록한 상품을 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 삭제 완료")
    })
    @DeleteMapping("/{productId}")
    public void delete(@AuthenticationPrincipal AuthenticatedAccount account,
                       @Parameter(name = "수정할 상품 아이디", in = ParameterIn.PATH) @PathVariable Long productId) {

        registrationService.delete(account.memberId(), productId);
    }
}
