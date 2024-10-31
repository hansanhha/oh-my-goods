package co.ohmygoods.sale.shop.exception;

public class InvalidShopNameException extends RuntimeException {

    public InvalidShopNameException(String message) {
        super(message);
    }

    public static InvalidShopNameException duplicate(String shopName) {
        return new InvalidShopNameException("duplicated shop name: ".concat(shopName));
    }

    public static InvalidShopNameException empty() {
        return new InvalidShopNameException("require shop name to create shop");
    }
}
