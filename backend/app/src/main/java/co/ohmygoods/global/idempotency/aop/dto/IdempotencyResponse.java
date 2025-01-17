package co.ohmygoods.global.idempotency.aop.dto;

import org.springframework.http.HttpStatusCode;

public record IdempotencyResponse(HttpStatusCode httpStatusCode,
                                  String responseBody) {
}
