package co.ohmygoods.order.service.dto;

import java.time.LocalDateTime;

public record OrderStartResponse(boolean isOrderSuccess,
                                 String nextProcessingURI,
                                 String orderTransactionId,
                                 String orderFailureCauseMessage,
                                 LocalDateTime orderedAt) {

    public static OrderStartResponse fail(String orderFailureCauseMessage) {
        return new OrderStartResponse(false, null, null, orderFailureCauseMessage, null);
    }

    public static OrderStartResponse success(String nextProcessingURI, String orderTransactionId, LocalDateTime orderedAt) {
        return new OrderStartResponse(true, nextProcessingURI, orderTransactionId, null, orderedAt);
    }
}
