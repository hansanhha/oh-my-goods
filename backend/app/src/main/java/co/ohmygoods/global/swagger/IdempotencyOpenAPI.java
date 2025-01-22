package co.ohmygoods.global.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

public interface IdempotencyOpenAPI {

    @Parameter(
            name = "멱등키",
            description = "UUIDv4와 같은 고유한 키 값이어야 합니다",
            in = ParameterIn.HEADER,
            required = true,
            example = "Idempotency-Key: 16a8a4b0-fd4a-44fc-b623-cf8527caa498")
    @interface HeaderDescription {

    }

    String message = "멱등성 준수를 위해 Idempotency-Key 헤더에 고유한 멱등키 값을 전달해야 합니다";
}
