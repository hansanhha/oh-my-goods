package co.ohmygoods.seller.product.exception;

import co.ohmygoods.global.exception.DomainException;
import co.ohmygoods.seller.shop.exception.SellerShopException;

public class SellerProductException extends DomainException {

    public static final SellerProductException NOT_FOUND_SELLER_PRODUCT = new SellerProductException(SellerProductError.NOT_FOUND_SELLER_PRODUCT);
    public static final SellerProductException INVALID_PRODUCT_QUANTITY = new SellerProductException(SellerProductError.INVALID_PRODUCT_QUANTITY);
    public static final SellerProductException INVALID_PRODUCT_STATUS = new SellerProductException(SellerProductError.INVALID_PRODUCT_STATUS);
    public static final SellerProductException INVALID_PRODUCT_PRICE = new SellerProductException(SellerProductError.INVALID_PRODUCT_PRICE);
    public static final SellerProductException INVALID_SUB_CATEGORY = new SellerProductException(SellerProductError.INVALID_SUB_CATEGORY);
    public static final SellerShopException DUPLICATE_CUSTOM_CATEGORY_NAME = new SellerShopException(SellerProductError.DUPLICATE_CUSTOM_CATEGORY_NAME);

    public SellerProductException(SellerProductError error) {
        super(error);
    }

    public static SellerProductException notFoundSellerProduct() {
        return NOT_FOUND_SELLER_PRODUCT;
    }
}
