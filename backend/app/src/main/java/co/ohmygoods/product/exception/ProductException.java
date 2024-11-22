package co.ohmygoods.product.exception;

public class ProductException extends RuntimeException {

    public ProductException() {
    }

    public ProductException(String message) {
        super(message);
    }

    public static void throwCauseInvalidDecreaseQuantity(int purchaseMaximumQuantity, int remainingQuantity, int quantity) {
        throw new ProductException();
    }

    public static void throwCauseInvalidIncreaseQuantity(int quantity) {
        throw new ProductException();
    }
}
