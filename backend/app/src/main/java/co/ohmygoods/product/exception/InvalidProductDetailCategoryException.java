package co.ohmygoods.product.exception;

public class InvalidProductDetailCategoryException extends RuntimeException {
    public InvalidProductDetailCategoryException(String message) {
        super(message);
    }

    public static InvalidProductDetailCategoryException duplicateName(String categoryName) {
        return new InvalidProductDetailCategoryException(categoryName);
    }
}
