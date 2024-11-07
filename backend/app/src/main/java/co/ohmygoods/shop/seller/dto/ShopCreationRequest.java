package co.ohmygoods.shop.seller.dto;

public record ShopCreationRequest(String ownerEmail,
                                  String shopName,
                                  String shopIntroduction) {
}
