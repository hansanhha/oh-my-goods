package co.ohmygoods.seller.product.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RegisterProductWebRequest(String productType,
                                        String productMainCategory,
                                        String productSubCategory,
                                        String productStatus,
                                        List<Long> productCustomCategoryIds,
                                        String productName,
                                        String productDescription,
                                        int productQuantity,
                                        int productPrice,
                                        int productDiscountRate,
                                        LocalDateTime productDiscountStartDate,
                                        LocalDateTime productDiscountEndDate,
                                        int productPurchaseLimitCount,
                                        LocalDateTime productExpectedSaleDate) {
}
