package co.ohmygoods.seller.product.service.dto;


import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.seller.product.controller.dto.RegisterProductWebRequest;
import co.ohmygoods.product.model.vo.ProductSubCategory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;



public record RegisterProductRequest(String ownerMemberId,
                                     ProductType type,
                                     ProductMainCategory mainCategory,
                                     ProductSubCategory subCategory,
                                     List<Long> customCategoryIds,
                                     String name,
                                     String description,
                                     MultipartFile[] assets,
                                     int quantity,
                                     int price,
                                     int purchaseLimitCount,
                                     int discountRate,
                                     boolean isImmediatelySale,
                                     LocalDateTime discountStartDate,
                                     LocalDateTime discountEndDate,
                                     LocalDateTime expectedSaleDate) {

    public static RegisterProductRequest of(String memberId, RegisterProductWebRequest request) {
        return new RegisterProductRequest(
            memberId, 
            ProductType.valueOf(request.productType()), 
            ProductMainCategory.valueOf(request.productMainCategory().toUpperCase()), 
            ProductSubCategory.valueOf(request.productSubCategory().toUpperCase()), 
            request.productCustomCategoryIds(), 
            request.productName(), 
            request.productDescription(), 
            request.productImages(), 
            request.productQuantity(), 
            request.productPrice(), 
            request.productPurchaseLimitCount(), 
            request.productDiscountRate(),
            request.productExpectedSaleDate() == null,
            request.productDiscountStartDate(), 
            request.productDiscountEndDate(),
            request.productExpectedSaleDate());
    }
}
