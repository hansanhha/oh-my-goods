package co.ohmygoods.order.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExchangeStatus {

    REQUESTED_EXCHANGING("교환 요청됨"),
    REJECTED_EXCHANGING("교환 거절됨"),
    EXCHANGED("교환됨");

    private final String message;
}
