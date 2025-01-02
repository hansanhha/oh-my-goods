package co.ohmygoods.seller.product.service.dto;

import co.ohmygoods.product.model.vo.ProductMainCategory;
import co.ohmygoods.product.model.vo.ProductStockStatus;
import co.ohmygoods.product.model.vo.ProductType;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record RegisterProductRequest(String ownerMemberId,
                                     ProductType type,
                                     ProductMainCategory mainCategory,
                                     String subCategory,
                                     boolean isImmediatelySale,
                                     List<Long> customCategoryIds,
                                     String name,
                                     String description,
                                     MultipartFile[] assets,
                                     int quantity,
                                     int price,
                                     int discountRate,
                                     LocalDateTime discountStartDate,
                                     LocalDateTime discountEndDate,
                                     int purchaseLimitCount,
                                     LocalDateTime expectedSaleDate) {
}
