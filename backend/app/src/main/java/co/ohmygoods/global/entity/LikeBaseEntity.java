package co.ohmygoods.global.entity;

import lombok.Getter;

@Getter
public abstract class LikeBaseEntity extends BaseEntity {

    private int like;

    public void like() {
        like++;
    }

    public void unlike() {
        like--;
    }
}
