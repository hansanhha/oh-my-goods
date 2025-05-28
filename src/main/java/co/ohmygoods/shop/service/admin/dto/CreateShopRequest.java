package co.ohmygoods.shop.service.admin.dto;

public record CreateShopRequest(String memberId,
                                String shopName,
                                String shopIntroduction) {
}
