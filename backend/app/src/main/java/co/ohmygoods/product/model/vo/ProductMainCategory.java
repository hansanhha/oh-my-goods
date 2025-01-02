package co.ohmygoods.product.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ProductMainCategory {

    MOVIE (
            Set.of()
    ),
    SPORTS (
            Set.of()
    ),
    IDOL (
            Set.of()
    ),
    INFLUENCER (
            Set.of()
    ),
    GAME (
            Set.of()
    ),
    MUSIC (
            Set.of()
    ),
    CONCERT_ART (
            Set.of()
    ),
    ANIMATION (
            Set.of()
    ),
    FASHION (
            Set.of()
    ),
    STATIONERY (
            Set.of()
    );

    private final Set<String> subCategories;

    public boolean contains(String subCategory) {
        return this.subCategories.contains(subCategory);
    }
}
