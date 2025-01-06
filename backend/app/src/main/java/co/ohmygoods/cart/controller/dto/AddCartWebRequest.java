package co.ohmygoods.cart.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddCartWebRequest(@NotNull @Positive(message = "올바르지 않은 상품 id입니다") Long productId) {
}
