package co.ohmygoods.seller.product.web.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.seller.product.service.SellerProductDiscountService;
import co.ohmygoods.seller.product.service.SellerProductRegistrationService;
import co.ohmygoods.seller.product.service.SellerProductStockService;

import co.ohmygoods.seller.product.service.dto.RegisterProductRequest;
import co.ohmygoods.seller.product.service.dto.SellerProductResponse;
import co.ohmygoods.seller.product.web.dto.RegisterProductWebRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.ohmygoods.product.model.vo.ProductMainCategory;

@RequestMapping("/api/seller/product")
@RestController
@RequiredArgsConstructor
public class SellerProductController {

    private final SellerProductRegistrationService registrationService;
    private final SellerProductStockService stockService;
    private final SellerProductDiscountService discountService;

    /*
        todo
            판매 상품 이미지 등록
            상품 메인/서브 카테고리 관리
     */
    @PostMapping
    public SellerProductResponse registerProduct(@AuthenticationPrincipal AuthenticatedAccount account,
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

        return registrationService.registerProduct(registerProductRequest);
    }
}
