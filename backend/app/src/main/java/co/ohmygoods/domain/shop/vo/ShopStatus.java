package co.ohmygoods.domain.shop.vo;

public enum ShopStatus {

    ACTIVE,
    INACTIVE,
    ENTIRE_SOLDOUT,
    DELETED;

    public boolean isChangeable(ShopStatus targetStatus) {
        if (this.equals(ShopStatus.DELETED)) {
            return targetStatus.equals(INACTIVE);
        }

        return true;
    }
}
