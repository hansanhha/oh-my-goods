package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.product.controller.admin.dto.ProductRegisterWebRequest;
import co.ohmygoods.product.model.vo.ProductSubCategory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;



public record ProductRegisterRequest(String ownerMemberId,
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

    public static ProductRegisterRequest of(String memberId, ProductRegisterWebRequest request) {
        return new ProductRegisterRequest(
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
