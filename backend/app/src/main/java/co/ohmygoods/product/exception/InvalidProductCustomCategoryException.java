package co.ohmygoods.product.exception;

public class InvalidProductCustomCategoryException extends RuntimeException {
    public InvalidProductCustomCategoryException(String message) {
        super(message);
    }

    public static InvalidProductCustomCategoryException duplicateName(String categoryName) {
        return new InvalidProductCustomCategoryException(categoryName);
    }
}
