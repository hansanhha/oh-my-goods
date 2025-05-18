package co.ohmygoods.seller.product.controller;

import co.ohmygoods.auth.account.model.vo.AuthenticatedAccount;
import co.ohmygoods.global.idempotency.aop.Idempotent;
import co.ohmygoods.global.swagger.IdempotencyOpenAPI;
import co.ohmygoods.global.swagger.PaginationOpenAPI;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductSubCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.seller.product.service.SellerProductRegistrationService;
import co.ohmygoods.seller.product.service.dto.RegisterProductRequest;
import co.ohmygoods.seller.product.service.dto.SellerProductResponse;
import co.ohmygoods.seller.product.service.dto.UpdateProductMetadataRequest;
import co.ohmygoods.seller.product.controller.dto.RegisterProductWebRequest;
import co.ohmygoods.seller.product.controller.dto.UpdateProductMetadataWebRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static co.ohmygoods.global.idempotency.aop.Idempotent.IDEMPOTENCY_HEADER;

@Tag(name = "판매자 상품", description = "판매자 상품 관련 api")
@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductRegistrationService registrationService;

    @Operation(summary = "판매자 상품 목록 조회", description = "판매자가 등록한 상품 목록을 최신순으로 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상품 목록 반환")
    })
    @GetMapping
    public ResponseEntity<?> getProducts(@AuthenticationPrincipal AuthenticatedAccount account,
                                         @PaginationOpenAPI.PageDescription @RequestParam(required = false, defaultValue = "0") int page,
                                         @PaginationOpenAPI.SizeDescription @RequestParam(required = false, defaultValue = "20") int size) {

        List<SellerProductResponse> registeredProducts = registrationService.getRegisteredProducts(account.memberId(), Pageable.ofSize(size).withPage(page));
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
                                             @RequestBody @Validated RegisterProductWebRequest request) {

        RegisterProductRequest registerProductRequest = RegisterProductRequest.builder()
                .ownerMemberId(account.memberId())
                .type(ProductType.valueOf(request.productType()))
                .mainCategory(ProductMainCategory.valueOf(request.productMainCategory().toUpperCase()))
                .subCategory(ProductSubCategory.valueOf(request.productSubCategory().toUpperCase()))
                .customCategoryIds(request.productCustomCategoryIds())
                .name(request.productName())
                .description(request.productDescription())
                .assets(request.productImages())
                .quantity(request.productQuantity())
                .price(request.productPrice())
                .purchaseLimitCount(request.productPurchaseLimitCount())
                .discountRate(request.productDiscountRate())
                .discountStartDate(request.productDiscountStartDate())
                .discountEndDate(request.productDiscountEndDate())
                .isImmediatelySale(request.productExpectedSaleDate() == null)
                .build();

        SellerProductResponse registered = registrationService.registerProduct(registerProductRequest);
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
                                                   @RequestBody @Validated UpdateProductMetadataWebRequest request) {

        UpdateProductMetadataRequest updateProductMetadataRequest = UpdateProductMetadataRequest.builder()
                .ownerMemberId(account.memberId())
                .updateProductId(productId)
                .updateName(request.updateProductName())
                .updateDescription(request.updateDescription())
                .updateType(ProductType.valueOf(request.updateProductType()))
                .updateMainCategory(ProductMainCategory.valueOf(request.updateProductMainCategory().toUpperCase()))
                .updateSubCategory(ProductSubCategory.valueOf(request.updateProductSubCategory().toUpperCase()))
                .updateCustomCategoryIds(request.updateProductCustomCategoryIds())
                .updateAssets(request.updateProductImages())
                .build();

        SellerProductResponse updated = registrationService.updateProductMetadata(updateProductMetadataRequest);
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
