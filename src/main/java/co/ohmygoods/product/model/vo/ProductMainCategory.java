package co.ohmygoods.product.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ProductMainCategory {

    MOVIE("영화"),
    SPORTS("스포츠"),
    IDOL("아이돌"),
    INFLUENCER("인플루언서"),
    GAME("게임"),
    MUSIC("음악"),
    CONCERT_ART("공연/예술"),
    ANIMATION("애니메이션"),
    FASHION("의류"),
    STATIONERY("문구");

    private final String displayName;

    public boolean notContains(ProductSubCategory subCategory) {
        return !subCategory.getParentCategory().equals(this);
    }
}
