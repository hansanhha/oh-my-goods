package co.ohmygoods.seller.product.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.seller.product.service.SellerProductRegistrationService;
import co.ohmygoods.seller.product.service.dto.RegisterProductRequest;
import co.ohmygoods.seller.product.service.dto.SellerProductResponse;
import co.ohmygoods.seller.product.service.dto.UpdateProductMetadataRequest;
import co.ohmygoods.seller.product.controller.dto.RegisterProductWebRequest;
import co.ohmygoods.seller.product.controller.dto.UpdateProductMetadataWebRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductRegistrationService registrationService;

    @GetMapping
    public ResponseEntity<?> getProducts(@AuthenticationPrincipal AuthenticatedAccount account,
                                         @RequestParam(required = false, defaultValue = "0") int page,
                                         @RequestParam(required = false, defaultValue = "20") int size) {

        List<SellerProductResponse> registeredProducts = registrationService.getRegisteredProducts(account.memberId(), Pageable.ofSize(size).withPage(page));
        return ResponseEntity.ok(registeredProducts);
    }

    @PostMapping
    public ResponseEntity<?> registerProduct(@AuthenticationPrincipal AuthenticatedAccount account,
                                             @RequestBody RegisterProductWebRequest request) {

        RegisterProductRequest registerProductRequest = RegisterProductRequest.builder()
                .ownerMemberId(account.memberId())
                .type(ProductType.valueOf(request.productType()))
                .mainCategory(ProductMainCategory.valueOf(request.productMainCategory()))
                .subCategory(request.productSubCategory())
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

    @PatchMapping("/{productId}/metadata")
    public ResponseEntity<?> updateProductMetadata(@AuthenticationPrincipal AuthenticatedAccount account,
                                                   @PathVariable Long productId,
                                                   @RequestBody UpdateProductMetadataWebRequest request) {

        UpdateProductMetadataRequest updateProductMetadataRequest = UpdateProductMetadataRequest.builder()
                .ownerMemberId(account.memberId())
                .updateProductId(productId)
                .updateName(request.updateProductName())
                .updateDescription(request.updateDescription())
                .updateType(ProductType.valueOf(request.updateProductType()))
                .updateMainCategory(ProductMainCategory.valueOf(request.updateProductMainCategory()))
                .updateSubCategory(request.updateProductSubCategory())
                .updateCustomCategoryIds(request.updateProductCustomCategoryIds())
                .updateAssets(request.updateProductImages())
                .build();

        SellerProductResponse updated = registrationService.updateProductMetadata(updateProductMetadataRequest);
        return ResponseEntity.ok(Map.of("data", updated));
    }

    @DeleteMapping("/{productId}")
    public void delete(@AuthenticationPrincipal AuthenticatedAccount account,
                                    @PathVariable Long productId) {

        registrationService.delete(account.memberId(), productId);
    }
}
