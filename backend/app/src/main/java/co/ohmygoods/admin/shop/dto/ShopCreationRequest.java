package co.ohmygoods.admin.shop.dto;

public record ShopCreationRequest(String ownerId,
                                  String shopName,
                                  String shopIntroduction) {
}
