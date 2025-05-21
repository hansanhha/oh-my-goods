package co.ohmygoods.shop.exception;


import co.ohmygoods.global.exception.DomainException;


public class ShopException extends DomainException {

    public static final ShopException NOT_FOUND_SHOP = new ShopException(ShopError.NOT_FOUND_SHOP);
    public static final ShopException ALREADY_EXIST_SHOP = new ShopException(ShopError.ALREADY_EXIST_SHOP);
    public static final ShopException ALREADY_EXIST_SHOP_OWNER = new ShopException(ShopError.ALREADY_EXIST_SHOP_OWNER);

    public ShopException(ShopError shopError) {
        super(shopError);
    }

    public static ShopException notFoundShop() {
        return NOT_FOUND_SHOP;
    }
}
