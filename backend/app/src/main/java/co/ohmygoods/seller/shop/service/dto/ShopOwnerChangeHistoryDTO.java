package co.ohmygoods.seller.shop.service.dto;

import co.ohmygoods.seller.shop.model.vo.ShopOwnerStatus;

import java.time.LocalDateTime;

public record ShopOwnerChangeHistoryDTO(Long historyId,
                                        String requestAccountEmail,
                                        String targetAccountEmail,
                                        ShopOwnerStatus status,
                                        LocalDateTime applicationDate) {
}
