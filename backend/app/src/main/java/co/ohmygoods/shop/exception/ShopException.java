package co.ohmygoods.shop.exception;

import co.ohmygoods.global.exception.DomainException;

public class ShopException extends DomainException {

    public static final ShopException NOT_FOUND_SHOP = new ShopException(ShopError.NOT_FOUND_SHOP);
    public static final ShopException INVALID_SHOP_NAME = new ShopException(ShopError.INVALID_SHOP_NAME);
    public static final ShopException INVALID_SHOP_STATUS = new ShopException(ShopError.INVALID_SHOP_STATUS);
    public static final ShopException INVALID_SHOP_OWNER = new ShopException(ShopError.INVALID_SHOP_OWNER);

    public ShopException(ShopError shopError) {
        super(shopError);
    }

    public static ShopException notFoundShop() {
        return NOT_FOUND_SHOP;
    }
}
