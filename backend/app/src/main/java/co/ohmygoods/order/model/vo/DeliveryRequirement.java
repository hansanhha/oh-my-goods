package co.ohmygoods.order.model.vo;

import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

public record DeliveryRequirement(String requirement) {

    public static final DeliveryRequirement AT_THE_DOOR = new DeliveryRequirement("문 앞에 놔주세요");
    public static final DeliveryRequirement SECURITY_OFFICE = new DeliveryRequirement("경비실에 맡겨주세요");
    public static final DeliveryRequirement DELIVERY_BOX = new DeliveryRequirement("택배 보관함 맡겨주세요");
    public static final DeliveryRequirement CALL_BEFORE_DELIVERY = new DeliveryRequirement("배송 전에 연락주세요");

    public static DeliveryRequirement directWrite(String requirement) {
        return new DeliveryRequirement(requirement);
    }

    private static final Map<DeliveryRequirement, String> instanceNames = new HashMap<>();

    static {
        instanceNames.put(AT_THE_DOOR, "AT_THE_DOOR");
        instanceNames.put(SECURITY_OFFICE, "SECURITY_OFFICE");
        instanceNames.put(DELIVERY_BOX, "DELIVERY_BOX");
        instanceNames.put(CALL_BEFORE_DELIVERY, "CALL_BEFORE_DELIVERY");
    }

    @Override
    public String toString() {
        return instanceNames.getOrDefault(this, requirement);
    }

    public static class DatabaseConverter implements AttributeConverter<DeliveryRequirement, String> {

        @Override
        public String convertToDatabaseColumn(DeliveryRequirement deliveryRequirement) {
            return deliveryRequirement != null ? deliveryRequirement.toString() : null;
        }

        @Override
        public DeliveryRequirement convertToEntityAttribute(String dbData) {
            if (dbData == null)
                return null;

            return switch (dbData) {
                case "AT_THE_DOOR" -> AT_THE_DOOR;
                case "SECURITY_OFFICE" -> SECURITY_OFFICE;
                case "DELIVERY_BOX" -> DELIVERY_BOX;
                case "CALL_BEFORE_DELIVERY" -> CALL_BEFORE_DELIVERY;
                default -> directWrite(dbData);
            };
        }
    }
}
