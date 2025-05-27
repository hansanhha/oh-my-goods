package co.ohmygoods.product.service.admin.dto;


import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import co.ohmygoods.product.controller.admin.dto.ProductRegisterWebRequest;
import co.ohmygoods.product.model.vo.ProductSubCategory;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;



public record ProductRegisterRequest(String adminMemberId,
                                     ProductType type,
                                     ProductMainCategory mainCategory,
                                     ProductSubCategory subCategory,
                                     List<Long> customCategoryIds,
                                     String name,
                                     String description,
                                     List<MultipartFile> assets,
                                     int quantity,
                                     int price,
                                     int purchaseLimitCount,
                                     int discountRate,
                                     boolean isImmediatelySale,
                                     LocalDateTime discountStartDate,
                                     LocalDateTime discountEndDate,
                                     LocalDateTime expectedSaleDate) {
}
