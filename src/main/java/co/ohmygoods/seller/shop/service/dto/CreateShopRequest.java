package co.ohmygoods.seller.shop.service.dto;

public record CreateShopRequest(String memberId,
                                String shopName,
                                String shopIntroduction) {
}
