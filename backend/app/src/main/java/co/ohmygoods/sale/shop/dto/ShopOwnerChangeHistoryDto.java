package co.ohmygoods.sale.shop.dto;

import co.ohmygoods.sale.shop.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistoryDto(Long historyId,
                                        String requestAccountEmail,
                                        String targetAccountEmail,
                                        ShopOwnerStatus status,
                                        LocalDateTime applicationDate) {
}
