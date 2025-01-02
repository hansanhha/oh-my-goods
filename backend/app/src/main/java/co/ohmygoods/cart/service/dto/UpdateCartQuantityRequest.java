package co.ohmygoods.cart.service.dto;

public record UpdateCartQuantityRequest(Long cartId,
                                        int updateQuantity) {
}
