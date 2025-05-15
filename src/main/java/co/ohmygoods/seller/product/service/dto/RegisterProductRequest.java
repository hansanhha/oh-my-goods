package co.ohmygoods.seller.product.service.dto;

import java.time.LocalDateTime;
import java.util.List;

import co.ohmygoods.product.model.vo.ProductSubCategory;
import org.springframework.web.multipart.MultipartFile;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;

@Builder
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
}
