package co.ohmygoods.product.exception;

import co.ohmygoods.product.vo.ProductStockStatus;

public class ProductStockStatusException extends RuntimeException {

    public ProductStockStatusException() {
    }

    public ProductStockStatusException(String message) {
        super(message);
    }


    public static void throwInvalidStatus(ProductStockStatus stockStatus) {
        throw new ProductStockStatusException();
    }
}
