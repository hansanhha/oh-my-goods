package co.ohmygoods.cart.service.dto;

public record AddCartRequest(Long productId,
                             String email) {
}
