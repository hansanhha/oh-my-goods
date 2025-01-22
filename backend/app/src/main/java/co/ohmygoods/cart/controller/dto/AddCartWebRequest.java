package co.ohmygoods.cart.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartWebRequest(
        @NotNull @Positive(message = "올바르지 않은 상품 id입니다")
        @Schema(description = "유효한 상품 아이디", example = "23")
        Long productId) {
}
