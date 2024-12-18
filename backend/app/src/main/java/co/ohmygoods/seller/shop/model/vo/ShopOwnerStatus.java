package co.ohmygoods.seller.shop.model.vo;

public enum ShopOwnerStatus {

    OWNER,
    OWNER_CHANGE_REQUESTED,
    OWNER_CHANGE_CANCELED,
    OWNER_CHANGE_APPROVED,
    OWNER_CHANGE_REJECTED;


    public boolean isChangeable(ShopOwnerStatus status) {
        switch (this) {
            case OWNER_CHANGE_APPROVED, OWNER_CHANGE_REJECTED -> {
                if (status.equals(OWNER_CHANGE_CANCELED) || status.equals(OWNER_CHANGE_REQUESTED))
                    return false;
            }

            case OWNER_CHANGE_CANCELED -> {
                return false;
            }
        };

        return true;
    }
}
