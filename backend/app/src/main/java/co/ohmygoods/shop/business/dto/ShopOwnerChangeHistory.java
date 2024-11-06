package co.ohmygoods.shop.business.dto;

import co.ohmygoods.shop.business.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistory(Long historyId,
                                     String requestAccountEmail,
                                     String targetAccountEmail,
                                     ShopOwnerStatus status,
                                     LocalDateTime applicationDate) {
}
