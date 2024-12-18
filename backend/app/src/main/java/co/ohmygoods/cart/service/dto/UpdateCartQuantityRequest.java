package co.ohmygoods.cart.service.dto;

public record UpdateCartQuantityRequest(Long cartId,
                                        String email,
                                        int updateQuantity) {
}
