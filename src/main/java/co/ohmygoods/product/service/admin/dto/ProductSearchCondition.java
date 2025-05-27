package co.ohmygoods.product.service.admin.dto;


public record ProductSearchCondition(
        String name,
        Boolean isOnSale,
        Boolean isOnDiscount) {

}
