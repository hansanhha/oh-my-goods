package co.ohmygoods.seller.product.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record RegisterProductWebRequest(String productType,
                                        String productMainCategory,
                                        String productSubCategory,
                                        String productStatus,
                                        List<Long> productCustomCategoryIds,
                                        String productName,
                                        String productDescription,
                                        MultipartFile[] productImages,
                                        int productQuantity,
                                        int productPrice,
                                        int productDiscountRate,
                                        int productPurchaseLimitCount,
                                        LocalDateTime productDiscountStartDate,
                                        LocalDateTime productDiscountEndDate,
                                        LocalDateTime productExpectedSaleDate) {
}
