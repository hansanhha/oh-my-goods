package co.ohmygoods.cart.dto;

public record UpdateCartQuantityRequest(Long cartId,
                                        String email,
                                        int updateQuantity) {
}
