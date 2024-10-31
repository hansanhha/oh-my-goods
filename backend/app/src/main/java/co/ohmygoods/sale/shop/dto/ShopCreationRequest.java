package co.ohmygoods.sale.shop.dto;

public record ShopCreationRequest(String ownerId,
                                  String shopName,
                                  String shopIntroduction) {
}
