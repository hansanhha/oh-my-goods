package co.ohmygoods.shop.model.vo;

public enum ShopStatus {

    ACTIVE,
    INACTIVE,
    ENTIRE_SOLDOUT,
    DELETE_SCHEDULED,
    DELETED;

    public boolean isChangeable(ShopStatus targetStatus) {
        if (this.equals(ShopStatus.DELETED)) {
            return !targetStatus.equals(INACTIVE);
        }

        return true;
    }
}
