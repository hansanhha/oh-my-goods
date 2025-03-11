package co.ohmygoods.global.idempotency.aop.dto;

public record IdempotencyRequest(String idempotencyKey,
                                 String httpMethod,
                                 String servletPath,
                                 String accessToken) {
}
