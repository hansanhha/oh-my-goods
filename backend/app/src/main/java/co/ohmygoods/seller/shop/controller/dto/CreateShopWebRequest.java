package co.ohmygoods.seller.shop.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateShopWebRequest(@NotEmpty(message = "올바르지 않은 상점 이름입니다")
                                   String createShopName,
                                   @NotEmpty(message = "올바르지 않은 상점 설명입니다")
                                   String createShopIntroduction) {
}
