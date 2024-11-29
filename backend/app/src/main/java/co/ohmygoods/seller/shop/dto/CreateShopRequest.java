package co.ohmygoods.seller.shop.dto;

public record CreateShopRequest(String ownerEmail,
                                String shopName,
                                String shopIntroduction) {
}
