package co.ohmygoods.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "요청 성공 여부와 오류에 대한 상세 정보를 가지고 있는 예외 응답")
public record ErrorResponse(

        @Schema(description = "요청 성공 여부", example = "false")
        boolean success,

        @Schema(description = "문제의 세부 설명")
        SimpleProblemDetail problemDetail) {

}
