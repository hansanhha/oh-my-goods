package co.ohmygoods.cart.exception;

import co.ohmygoods.global.exception.DomainException;

public class CartException extends DomainException {

    public static final CartException NOT_FOUND_CART = new CartException(CartError.NOT_FOUND_CART);
    public static final CartException ALREADY_EXIST_PRODUCT = new CartException(CartError.ALREADY_EXIST_PRODUCT);
    public static final CartException EXCEED_CART_MAX_LIMIT = new CartException(CartError.EXCEED_CART_MAX_LIMIT);
    public static final CartException EXCEED_PRODUCT_MAX_LIMIT = new CartException(CartError.EXCEED_PRODUCT_MAX_LIMIT);

    public CartException(CartError cartError) {
        super(cartError);
    }

    public static CartException notFoundCart() {
        return NOT_FOUND_CART;
    }
}
