package co.ohmygoods.shop.exception;

public class UnchangeableShopOwnerException extends RuntimeException {
    public UnchangeableShopOwnerException(String message) {
        super(message);
    }

    public static UnchangeableShopOwnerException isNotOwner(String accountEmail, String shopName) {
        return new UnchangeableShopOwnerException(accountEmail.concat("is not ").concat(shopName).concat(" shop owner"));
    }

    public static UnchangeableShopOwnerException unchangeable(String originalStatus, String targetStatus) {
        return new UnchangeableShopOwnerException("cannot cancel in ".concat(targetStatus).concat(" status"));
    }

    public static UnchangeableShopOwnerException isNotTargetAccount(String accountEmail, String shopName) {
        return new UnchangeableShopOwnerException(accountEmail.concat("is not target account for ").concat(shopName));
    }
}
