package co.ohmygoods.cart.service.dto;

public record CartDto(Long shopId,
                      Long productId,
                      String productName,
                      int originalPrice,
                      int discountPrice,
                      int containQuantity,
                      int orderableQuantity) {
}
