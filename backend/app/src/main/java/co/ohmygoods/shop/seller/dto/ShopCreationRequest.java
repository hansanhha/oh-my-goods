package co.ohmygoods.shop.seller.dto;

public record ShopCreationRequest(String ownerId,
                                  String shopName,
                                  String shopIntroduction) {
}
