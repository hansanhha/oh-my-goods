package co.ohmygoods.admin.shop.dto;

import co.ohmygoods.domain.shop.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistory(Long historyId,
                                     String requestAccountEmail,
                                     String targetAccountEmail,
                                     ShopOwnerStatus status,
                                     LocalDateTime applicationDate) {
}
