package co.ohmygoods.payment.service;


import co.ohmygoods.payment.exception.PaymentException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import java.io.IOException;
import java.io.InputStream;


public class PaymentAPIUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static byte[] convertToJson(Object requestBody) {
        try {
            return objectMapper.writeValueAsBytes(requestBody);
        } catch (JsonProcessingException e) {
            throw PaymentException.FAILED_PAYMENT_API_REQUEST;
        }
    }

    static <T> T convertToDTO(InputStream apiResponseBody, Class<T> dtoType) {
        try {
            return objectMapper.readValue(apiResponseBody, dtoType);
        } catch (IOException e) {
            throw PaymentException.FAILED_PAYMENT_API_REQUEST;
        }
    }

    enum PaymentPhase {
        PREPARE,
        APPROVE,
        ERROR
    }

}
