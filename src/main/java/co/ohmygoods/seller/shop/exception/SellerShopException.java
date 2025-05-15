package co.ohmygoods.seller.shop.exception;

import co.ohmygoods.global.exception.DomainException;

public class SellerShopException extends DomainException {

    public static final SellerShopException NOT_FOUND_SHOP = new SellerShopException(SellerShopError.NOT_FOUND_SHOP);
    public static final SellerShopException INVALID_SHOP_CREATION_INFO = new SellerShopException(SellerShopError.INVALID_SHOP_CREATION_INFO);
    public static final SellerShopException ALREADY_EXIST_SHOP = new SellerShopException(SellerShopError.ALREADY_EXIST_SHOP);
    public static final SellerShopException ALREADY_EXIST_SHOP_OWNER = new SellerShopException(SellerShopError.ALREADY_EXIST_SHOP_OWNER);
    public static final SellerShopException INVALID_SHOP_STATUS = new SellerShopException(SellerShopError.INVALID_SHOP_STATUS);

    public SellerShopException(SellerShopError error) {
        super(error);
    }

    public static SellerShopException notFoundShop() {
        return NOT_FOUND_SHOP;
    }
}
