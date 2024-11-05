package co.ohmygoods.sale.shop.exception;

public class InvalidShopOwnerException extends RuntimeException {
    public InvalidShopOwnerException(String message) {
        super(message);
    }

    public static InvalidShopOwnerException isNotOwner(String accountEmail, String shopName) {
        return new InvalidShopOwnerException(accountEmail.concat("is not ").concat(shopName).concat(" shop owner"));
    }
}
