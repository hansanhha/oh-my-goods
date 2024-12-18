package co.ohmygoods.seller.shop.service.dto;

public record CreateShopRequest(String ownerEmail,
                                String shopName,
                                String shopIntroduction) {
}
