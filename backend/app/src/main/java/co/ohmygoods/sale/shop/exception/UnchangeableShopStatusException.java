package co.ohmygoods.sale.shop.exception;

public class UnchangeableShopStatusException extends RuntimeException {
    public UnchangeableShopStatusException(String message) {
        super(message);
    }

    public static UnchangeableShopStatusException unchangeable(String originalStatus, String targetStatus) {
        return new UnchangeableShopStatusException("unable change status from ".concat(originalStatus).concat(" to ").concat(targetStatus));
    }

    public static UnchangeableShopStatusException unchangeable(String targetStatus) {
        return new UnchangeableShopStatusException("unable change status to".concat(targetStatus));
    }
}
