package co.ohmygoods.order.service.dto;

import java.time.LocalDateTime;

public record OrderCheckoutResponse(boolean isOrderSuccess,
                                    String nextProcessingURI,
                                    String orderTransactionId,
                                    String orderFailureCauseMessage,
                                    LocalDateTime orderedAt) {

    public static OrderCheckoutResponse fail(String orderFailureCauseMessage) {
        return new OrderCheckoutResponse(false, null, null, orderFailureCauseMessage, null);
    }

    public static OrderCheckoutResponse success(String nextProcessingURI, String orderTransactionId, LocalDateTime orderedAt) {
        return new OrderCheckoutResponse(true, nextProcessingURI, orderTransactionId, null, orderedAt);
    }
}
