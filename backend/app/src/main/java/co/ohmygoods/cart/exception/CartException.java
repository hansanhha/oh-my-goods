package co.ohmygoods.cart.exception;

public class CartException extends RuntimeException {
    public CartException() {
    }

    public CartException(String message) {
        super(message);
    }

    public static CartException invalidProductStatus(Long productId) {
        return new CartException(productId.toString());
    }

    public static CartException notFound(Long cartId) {
        return new CartException(cartId.toString());
    }

    public static CartException exceedProductMaximumQuantity(Long productId, int maximumQuantity) {
        return new CartException(productId.toString() + maximumQuantity);
    }

    public static CartException invalidQuantity(Long cartId, int quantity) {
        return new CartException(cartId.toString() + quantity);
    }

    public static CartException exceedCartMaximumQuantity() {
        return new CartException("");
    }
}
