package co.ohmygoods.global.idempotency.service.dto;

public record IdempotencyRequest(String idempotencyKey,
                                 String httpMethod,
                                 String servletPath,
                                 String accessToken) {
}
