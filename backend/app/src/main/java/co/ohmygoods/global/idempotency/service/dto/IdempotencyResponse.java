package co.ohmygoods.global.idempotency.service.dto;

import org.springframework.http.HttpStatusCode;

public record IdempotencyResponse(HttpStatusCode httpStatusCode,
                                  String responseBody) {
}
