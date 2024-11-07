package co.ohmygoods.shop.seller.dto;

import co.ohmygoods.shop.seller.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistory(Long historyId,
                                     String requestAccountEmail,
                                     String targetAccountEmail,
                                     ShopOwnerStatus status,
                                     LocalDateTime applicationDate) {
}
