package co.ohmygoods.shop.controller.admin.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;


public record ShopCreateWebRequest(

        @Schema(description = "상점 이름")
        @NotEmpty(message = "올바르지 않은 상점 이름입니다")
        String createShopName,

        @Schema(description = "상점 설명")
        @NotEmpty(message = "올바르지 않은 상점 설명입니다")
        String createShopIntroduction) {
}
