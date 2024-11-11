package co.ohmygoods.cart.dto;

public record AddCartRequest(Long productId,
                             String email) {
}
