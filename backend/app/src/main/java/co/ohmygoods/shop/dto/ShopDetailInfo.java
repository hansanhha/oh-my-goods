package co.ohmygoods.shop.dto;

import co.ohmygoods.shop.vo.ShopStatus;

import java.time.LocalDateTime;

public record ShopDetailInfo(String name,
                             String introduction,
                             LocalDateTime createdAt,
                             String shopImage,
                             ShopStatus status) {
}