package co.ohmygoods.shop.service.admin.dto;


import co.ohmygoods.product.model.entity.Product;
import co.ohmygoods.shop.model.entity.Shop;
import co.ohmygoods.shop.model.vo.ShopStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public record ShopOverviewResponse(String name,
                                   String introduction,
                                   LocalDateTime createdAt,
                                   String shopImage,
                                   ShopStatus status,
                                   Map<Long, String> shopAllProductCategories,
                                   List<Product> products) {

    public static ShopOverviewResponse of(Shop shop, Map<Long, String> categories, List<Product> products) {
        return new ShopOverviewResponse(
            shop.getName(), 
            shop.getIntroduction(), 
            shop.getCreatedAt(), 
            shop.getShopImageName().concat(shop.getIntroduction()), 
            shop.getStatus(), 
            categories, 
            products);
    }
}
