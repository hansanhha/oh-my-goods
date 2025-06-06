package co.ohmygoods.product.exception;

import co.ohmygoods.global.exception.DomainException;

public class ProductException extends DomainException {

    public static final ProductException NOT_FOUND_PRODUCT = new ProductException(ProductError.NOT_FOUND_PRODUCT);
    public static final ProductException NOT_FOUND_CATEGORY = new ProductException(ProductError.NOT_FOUND_CATEGORY);

    public static final ProductException CANNOT_UPDATE_PRODUCT_STATUS = new ProductException(ProductError.CANNOT_UPDATE_PRODUCT_STATUS);

    public static final ProductException EXCEED_PURCHASE_PRODUCT_MAX_LIMIT = new ProductException(ProductError.EXCEED_PURCHASE_PRODUCT_MAX_LIMIT);
    public static final ProductException INVALID_PURCHASE_QUANTITY = new ProductException(ProductError.INVALID_PURCHASE_QUANTITY);
    public static final ProductException INVALID_PRODUCT_STATUS = new ProductException(ProductError.INVALID_PRODUCT_STATUS);
    public static final ProductException INVALID_PRODUCT_QUANTITY = new ProductException(ProductError.INVALID_PRODUCT_QUANTITY);
    public static final ProductException INVALID_METADATA = new ProductException(ProductError.INVALID_METADATA);

    public static final ProductException NOT_SALES_STATUS = new ProductException(ProductError.NOT_SALES_STATUS);
    public static final ProductException NOT_ENOUGH_STOCK = new ProductException(ProductError.NOT_ENOUGH_STOCK);

    public static final ProductException DUPLICATE_CUSTOM_CATEGORY_NAME = new ProductException(ProductError.DUPLICATE_CUSTOM_CATEGORY_NAME);
    public static final ProductException INVALID_PRODUCT_PRICE = new ProductException(ProductError.INVALID_PRODUCT_PRICE);
    public static final ProductException INVALID_SUB_CATEGORY = new ProductException(ProductError.INVALID_SUB_CATEGORY);

    public ProductException(ProductError error) {
        super(error);
    }

    public static ProductException notFoundProduct() {
        return NOT_FOUND_PRODUCT;
    }

    public static ProductException notFoundCategory() {
        return NOT_FOUND_CATEGORY;
    }

}
