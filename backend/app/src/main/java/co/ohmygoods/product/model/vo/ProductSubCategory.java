package co.ohmygoods.product.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSubCategory {

    // MOVIE
    MOVIE_POSTER(ProductMainCategory.MOVIE, "영화 포스터"),
    MOVIE_TICKET(ProductMainCategory.MOVIE, "영화 티켓"),
    MOVIE_GOODS(ProductMainCategory.MOVIE, "영화 굿즈"),
    MOVIE_DVD(ProductMainCategory.MOVIE, "영화 DVD/블루레이"),
    MOVIE_LIMITED(ProductMainCategory.MOVIE, "영화 한정판 상품"),

    // SPORTS
    SPORTS_UNIFORM(ProductMainCategory.SPORTS, "스포츠 유니폼"),
    SPORTS_EQUIPMENT(ProductMainCategory.SPORTS, "스포츠 장비"),
    SPORTS_SIGNED_BALL(ProductMainCategory.SPORTS, "스포츠 사인볼"),
    SPORTS_CHEERING_TOOL(ProductMainCategory.SPORTS, "스포츠 응원 도구"),
    SPORTS_DVD(ProductMainCategory.SPORTS, "스포츠 DVD"),

    // IDOL
    IDOL_FAN_CLUB_GOODS(ProductMainCategory.IDOL, "아이돌 팬클럽 굿즈"),
    IDOL_ALBUM(ProductMainCategory.IDOL, "아이돌 앨범"),
    IDOL_PHOTOCARD(ProductMainCategory.IDOL, "아이돌 포토카드"),
    IDOL_SIGNED_POSTER(ProductMainCategory.IDOL, "아이돌 사인 포스터"),
    IDOL_FIGURE(ProductMainCategory.IDOL, "아이돌 피규어"),

    // INFLUENCER
    INFLUENCER_GOODS(ProductMainCategory.INFLUENCER, "인플루언서 굿즈"),
    INFLUENCER_LIMITED(ProductMainCategory.INFLUENCER, "인플루언서 한정판 상품"),
    INFLUENCER_FAN_ART(ProductMainCategory.INFLUENCER, "인플루언서 팬아트"),
    INFLUENCER_LIVE_ITEM(ProductMainCategory.INFLUENCER, "인플루언서 라이브 방송 아이템"),
    INFLUENCER_PLANNED_ITEM(ProductMainCategory.INFLUENCER, "인플루언서 기획 상품"),

    // GAME
    GAME_CD(ProductMainCategory.GAME, "게임 CD"),
    GAME_GOODS(ProductMainCategory.GAME, "게임 굿즈"),
    GAME_FIGURE(ProductMainCategory.GAME, "게임 피규어"),
    GAME_LIMITED_CONSOLE(ProductMainCategory.GAME, "한정판 콘솔"),
    GAME_KEYBOARD_MOUSE(ProductMainCategory.GAME, "키보드/마우스"),

    // MUSIC
    MUSIC_ALBUM(ProductMainCategory.MUSIC, "음반"),
    MUSIC_LP(ProductMainCategory.MUSIC, "LP"),
    MUSIC_GOODS(ProductMainCategory.MUSIC, "음악 굿즈"),
    MUSIC_CONCERT_DVD(ProductMainCategory.MUSIC, "콘서트 DVD"),
    MUSIC_CD(ProductMainCategory.MUSIC, "음악 CD"),

    // CONCERT_ART
    CONCERT_TICKET(ProductMainCategory.CONCERT_ART, "공연 티켓"),
    CONCERT_ARTBOOK(ProductMainCategory.CONCERT_ART, "아트북"),
    CONCERT_PROGRAM(ProductMainCategory.CONCERT_ART, "공연 프로그램"),
    CONCERT_POSTER(ProductMainCategory.CONCERT_ART, "공연 포스터"),
    CONCERT_GOODS(ProductMainCategory.CONCERT_ART, "공연 굿즈"),

    // ANIMATION
    ANIMATION_FIGURE(ProductMainCategory.ANIMATION, "애니메이션 피규어"),
    ANIMATION_GOODS(ProductMainCategory.ANIMATION, "애니메이션 굿즈"),
    ANIMATION_DVD(ProductMainCategory.ANIMATION, "애니메이션 DVD/블루레이"),
    ANIMATION_POSTER(ProductMainCategory.ANIMATION, "애니메이션 포스터"),
    ANIMATION_ARTBOOK(ProductMainCategory.ANIMATION, "애니메이션 아트북"),

    // FASHION
    FASHION_TSHIRT(ProductMainCategory.FASHION, "티셔츠"),
    FASHION_HOODIE(ProductMainCategory.FASHION, "후드"),
    FASHION_HAT(ProductMainCategory.FASHION, "모자"),
    FASHION_ACCESSORY(ProductMainCategory.FASHION, "액세서리"),
    FASHION_BAG(ProductMainCategory.FASHION, "가방"),

    // STATIONERY
    STATIONERY_DIARY(ProductMainCategory.STATIONERY, "다이어리"),
    STATIONERY_NOTE(ProductMainCategory.STATIONERY, "노트"),
    STATIONERY_PEN(ProductMainCategory.STATIONERY, "펜"),
    STATIONERY_STICKER(ProductMainCategory.STATIONERY, "스티커"),
    STATIONERY_DESK_MAT(ProductMainCategory.STATIONERY, "데스크 매트");

    private final ProductMainCategory parentCategory;
    private final String displayName;
}
