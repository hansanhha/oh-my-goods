package co.ohmygoods.seller.shop.dto;

import co.ohmygoods.seller.shop.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistoryDTO(Long historyId,
                                        String requestAccountEmail,
                                        String targetAccountEmail,
                                        ShopOwnerStatus status,
                                        LocalDateTime applicationDate) {
}
