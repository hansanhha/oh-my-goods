package co.ohmygoods.cart.service.dto;


public record CartResponse(Long shopId,
                           String shopName,
                           Long productId,
                           String productName,
                           Long cartId,
                           int cartOriginalPrice,
                           int cartDiscountedPrice,
                           int quantity,
                           int orderableQuantity) {
}
