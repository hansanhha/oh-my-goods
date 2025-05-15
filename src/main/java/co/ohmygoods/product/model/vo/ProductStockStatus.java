package co.ohmygoods.product.model.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStockStatus {

    ON_SALES("판매 중"),
    TO_BE_SOLD("판매 예정"),
    SOLDOUT("품절"),
    TO_BE_RESTOCKED("재입고 예정"),
    RESTOCKED("재입고");

    private final String message;

}
