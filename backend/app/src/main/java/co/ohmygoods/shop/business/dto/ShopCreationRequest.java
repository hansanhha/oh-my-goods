package co.ohmygoods.shop.business.dto;

public record ShopCreationRequest(String ownerId,
                                  String shopName,
                                  String shopIntroduction) {
}
