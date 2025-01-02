package co.ohmygoods.seller.product.web.controller;

import co.ohmygoods.auth.jwt.service.AuthenticatedAccount;
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

                .build();

        return registrationService.registerProduct(registerProductRequest);
    }
}
