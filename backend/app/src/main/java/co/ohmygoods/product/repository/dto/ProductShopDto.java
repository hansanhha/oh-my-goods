package co.ohmygoods.product.repository.dto;

import co.ohmygoods.product.model.entity.Product;

public record ProductShopDto(
        Long shopId,
        String shopName,
        Product product) {

}
